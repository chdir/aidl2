package net.sf.aidl2.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.NameAllocator;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.ArgumentKind;
import net.sf.aidl2.InterfaceLoader;
import net.sf.aidl2.internal.codegen.Blocks;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.codegen.TypedExpression;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import static net.sf.aidl2.internal.ContractHasher.*;
import static net.sf.aidl2.internal.util.Util.*;

final class Reader extends AptHelper {
    private final ClassName textUtils = ClassName.get("android.text", "TextUtils");

    private final boolean allowUnchecked;
    private final boolean external;
    private final boolean nullable;
    private final boolean assumeFinal;

    private final DeclaredType parcelable;

    private final TypeMirror sizeF;
    private final TypeMirror sizeType;
    private final TypeMirror iBinder;
    private final TypeMirror bundle;
    private final TypeMirror persistable;
    private final TypeMirror sparseBoolArray;

    private final TypeMirror string;
    private final TypeMirror charSequence;

    private final TypeMirror intType;
    private final TypeMirror floatType;

    protected final DeclaredType genericCreator;

    private final CharSequence parcelName;
    private final CharSequence selfName;
    private final NameAllocator allocator;

    private final TypeInvocation<ExecutableElement, ExecutableType> collectionAdd;
    private final TypeInvocation<ExecutableElement, ExecutableType> mapPut;

    private final DataOutputStream versionCalc;

    public Reader(AidlProcessor.Environment environment, State state, CharSequence parcelName) {
        super(environment);

        this.parcelName = parcelName;

        this.selfName = state.name;
        this.allowUnchecked = state.allowUnchecked;
        this.nullable = state.nullable;
        this.external = state.external;
        this.allocator = state.allocator;
        this.assumeFinal = state.assumeFinal;
        this.versionCalc = state.versionCalc();

        this.sizeType = lookup("android.util.Size");
        this.sizeF = lookup("android.util.SizeF");
        this.parcelable = lookup("android.os.Parcelable");
        this.iBinder = lookup("android.os.IBinder");
        this.bundle = lookup("android.os.Bundle");
        this.persistable = lookup("android.os.PersistableBundle");
        this.sparseBoolArray = lookup("android.util.SparseBooleanArray");

        this.string = lookup(String.class);
        this.charSequence = lookup(CharSequence.class);

        final TypeElement creatorType = lookupGeneric("android.os.Parcelable.Creator");
        final TypeMirror bound = types.getWildcardType(null, parcelable);
        genericCreator = types.getDeclaredType(creatorType, bound);

        collectionAdd = lookupMethod(theCollection, "add", boolean.class, "Object");
        mapPut = lookupMethod(theMap, "put", "Object", "Object", "Object");

        intType = types.getPrimitiveType(TypeKind.INT);
        floatType = types.getPrimitiveType(TypeKind.FLOAT);
    }

    /**
     * Read a single return value from parcel.
     */
    public TypedExpression read(CodeBlock.Builder block, TypeMirror type) throws CodegenException, IOException {
        final Strategy strategy = getStrategy(type);

        if (strategy != null) {
            final CodeBlock assignment;

            if (nullable && strategy.needNullHandling) {
                assignment = getNullableStrategy(type, strategy).read(block);
            } else {
                assignment = strategy.read(block);
            }

            strategy.writeState(versionCalc);

            return new TypedExpression(assignment, strategy.returnType);
        }

        throw new CodegenException("Unsupported type: " + type + ".\n" + getHelpText());
    }

    private Strategy getNullableStrategy(TypeMirror type, Strategy inner) {
        if (inner == VOID_STRATEGY) {
            return inner;
        }

        // strip generics to prevent them from messing up creation code
        final TypeMirror capturedType = captureAll(type);

        return new NullableWrapper(capturedType, inner);
    }

    private final class NullableWrapper extends Strategy {
        private final TypeMirror captured;
        private final Strategy inner;

        private NullableWrapper(TypeMirror captured, Strategy delegate) {
            super(null, delegate.returnType, delegate.kind, false);

            this.captured = captured;
            this.inner = delegate;
        }

        @Override
        public CodeBlock read(CodeBlock.Builder init) throws CodegenException {
            final String tmp = allocator.newName(selfName + "Tmp");

            init.addStatement("final $T $N", captured, tmp);
            init.beginControlFlow("if ($N.readByte() == -1)", parcelName);
            init.addStatement("$N = null", tmp);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", tmp, emitCasts(inner.returnType, captured, inner.read(init)));
            init.endControlFlow();

            return literal(tmp);
        }

        @Override
        public void writeState(DataOutputStream versionCalc) throws IOException {
            versionCalc.write(NULL_CHECK);

            inner.writeState(versionCalc);
        }
    }

    private @Nullable Strategy getStrategy(TypeMirror type) throws CodegenException {
        switch (type.getKind()) {
            case BOOLEAN:
            case INT:
            case SHORT:
            case BYTE:
            case CHAR:
            case LONG:
            case DOUBLE:
            case FLOAT:
                return createNullSafe($ -> readPrimitive((PrimitiveType) type), type);
            case ARRAY:
                return getArrayStrategy((ArrayType) type);
            case DECLARED:
                final Strategy wrapperTypeStrategy = getWrapperTypeStrategy((DeclaredType) type);

                if (wrapperTypeStrategy != null) {
                    return wrapperTypeStrategy;
                }
            default:
                final Strategy specialStrategy = getBuiltinStrategy(type);

                if (specialStrategy != null) {
                    return specialStrategy;
                }

                // Always prefer Parcelable reading strategy
                if (types.isAssignable(type, parcelable)) {
                    final DeclaredType concreteParent = findConcreteParent(type, parcelable);

                    if (concreteParent != null) {
                        final Strategy strategy = getParcelableStrategy(concreteParent);

                        if (strategy != null) {
                            return strategy;
                        }
                    }

                    return getUnknownParcelableStrategy(type);
                }

                // Or IInterface reading strategy
                if (types.isAssignable(type, theIInterface)) {
                    final Strategy strategy = getIInterfaceStrategy(type);

                    if (strategy != null) {
                        return strategy;
                    }
                }

                // Or at least Externalizable strategy
                if (types.isAssignable(type, externalizable)) {
                    final DeclaredType concreteParent = findConcreteParent(type, externalizable);

                    if (concreteParent != null) {
                        final Strategy strategy = getExternalizableStrategy(concreteParent);

                        if (strategy != null) {
                            return strategy;
                        }
                    }

                    return getUnknownExternalizableStrategy(type);
                }

                // check for parameters, that resolve to wrapper types...
                // intersection types go to hell
                final TypeMirror erased = types.erasure(type);

                if (erased.getKind() == TypeKind.DECLARED) {
                    final Strategy typeArgWrapperTypeStr = getWrapperTypeStrategy((DeclaredType) erased);

                    if (typeArgWrapperTypeStr != null) {
                        return typeArgWrapperTypeStr;
                    }
                }

                // Check for Map subtypes
                if (types.isAssignable(type, theMap)) {
                    final Strategy strategy = getMapStrategy(type);

                    if (strategy != null) {
                        return strategy;
                    }
                }

                // Check for Collection subtypes
                if (types.isAssignable(type, theCollection)) {
                    final Strategy strategy = getCollectionStrategy(type);

                    if (strategy != null) {
                        return strategy;
                    }
                }

                if (types.isAssignable(type, serializable)) {
                    return getSerializableStrategy(type);
                }
        }

        return null;
    }

    private final Strategy VOID_STRATEGY = new Strategy(
            $ -> literal("null"),
            types.getNullType(),
            ArgumentKind.AUTO, false)
    {
        public void writeState(DataOutputStream versionCalc) throws IOException {}
    };

    private Strategy getWrapperTypeStrategy(DeclaredType type) {
        final CharSequence name = getQualifiedName(type);
        if (name != null) {
            switch (name.toString()) {
                case "java.lang.Boolean":
                case "java.lang.Byte":
                case "java.lang.Short":
                case "java.lang.Integer":
                case "java.lang.Long":
                case "java.lang.Character":
                case "java.lang.Float":
                case "java.lang.Double":
                    // Handle Integer, Long, Character etc. before falling back to Serializable path
                    final PrimitiveType primitiveVariety = types.unboxedType(type);

                    if (primitiveVariety != null) {
                        return newStrategy($ -> readPrimitive(primitiveVariety), primitiveVariety);
                    }
                    break;
                case "java.lang.Void":
                    return VOID_STRATEGY;
            }
        }

        return null;
    }

    private Strategy getIInterfaceStrategy(TypeMirror type) throws CodegenException {
        final DeclaredType declared = findDeclaredParent(type, theIInterface);

        if (declared == null || types.isSameType(declared, theIInterface)) {
            throw new CodegenException("Can not pass unknown android.os.IInterface subtype over IPC. Try to use more specific type.");
        }

        final TypeElement el = (TypeElement) declared.asElement();

        if (Util.getAnnotation(el, AIDL.class) != null) {
            return getInterfaceLoaderStrategy(declared, Util.isNullable(type, nullable));
        }

        final TypeElement stubClass = lookupStaticClass(declared, "Stub", theBinder);

        if (stubClass != null) {
            if (lookupStaticMethod((DeclaredType) stubClass.asType(), "asInterface", theIInterface, "IBinder") != null) {
                return getOldIInterfaceStrategy(stubClass);
            }
        }

        getMessager().printMessage(Diagnostic.Kind.WARNING, "Type " + type + " implements IInterface, " +
                "but has neither @AIDL annotation nor nested class, called 'Stub' — can not deserialize as IInterface");

        return null;
    }

    private Strategy getOldIInterfaceStrategy(TypeElement stubClass) {
        final ReadingStrategy s = block -> literal("$T.asInterface($N.readStrongBinder())", stubClass, parcelName);

        return createNullSafe(s, stubClass.getEnclosingElement().asType(), ArgumentKind.BINDER);
    }

    private Strategy getInterfaceLoaderStrategy(DeclaredType type, boolean nullable) {
        final TypeMirror raw = types.erasure(type);

        final ReadingStrategy strategy;
        if (nullable) {
            final TypeMirror captured = captureAll(type);

            strategy = init -> {
                final String tmpBinder = allocator.newName(selfName + "Binder");

                init.addStatement("final $T $N = $N.readStrongBinder()", iBinder, tmpBinder, parcelName);

                final String tmpInterface = allocator.newName("i" +
                        Character.toUpperCase(selfName.charAt(0)) + selfName.subSequence(1, selfName.length()));

                init.addStatement("final $T $N = $N == null ? null : $T.asInterface($N, $T.class)",
                        captured, tmpInterface, tmpBinder, InterfaceLoader.class, tmpBinder, raw);

                return literal(tmpInterface);
            };

            return createNullSafe(strategy, jokeLub(captured, theIInterface), ArgumentKind.BINDER);
        } else {
            strategy = block -> literal("$T.asInterface($N.readStrongBinder(), $T.class)",
                    InterfaceLoader.class, parcelName, raw);

            return createNullSafe(strategy, jokeLub(type, theIInterface), ArgumentKind.BINDER);
        }
    }

    private Strategy getExternalizableStrategy(DeclaredType type) throws CodegenException {
        final TypeElement clazz = (TypeElement) type.asElement();

        final boolean trulyFinal = clazz.getModifiers().contains(Modifier.FINAL);

        if (!hasDefaultConstructor(clazz)) {
            if (trulyFinal) {
                throw new CodegenException("Externalizable class does not have accessible constructor");
            } else {
                getMessager().printMessage(Diagnostic.Kind.WARNING, "Type " + type + " implements Externalizable, " +
                        "but can not be deserialize as such (no public default constructor)");

                return null;
            }
        }

        if (!trulyFinal && !assumeFinal) {
            return null;
        }

        // strip generics to prevent them from messing up creation code
        final TypeMirror capturedType = captureAll(type);

        return newStrategy(block -> {
            final String ois = allocator.newName("objectInputStream");
            final String xtrnlzbl = allocator.newName(selfName + "Externalizable");
            final String err = allocator.newName("e");

            block.addStatement("$T $N = null", ObjectInputStream.class, ois);
            block.addStatement("$T $N = null", capturedType, xtrnlzbl);

            block.beginControlFlow("try");

            block.addStatement("$N = new $T(new $T($N.createByteArray()))", ois,
                    ObjectInputStream.class, ByteArrayInputStream.class, parcelName);
            block.addStatement("$N = $L", xtrnlzbl, emitDefaultConstructorCall(capturedType));
            block.addStatement("$N.readExternal($N)", xtrnlzbl, ois);

            block.nextControlFlow("catch (Exception $N)", err);

            block.addStatement("throw new IllegalStateException($S, $N)", "Failed to deserialize " + type, err);

            block.nextControlFlow("finally");
            block.addStatement("$T.shut($N)", AidlUtil.class, ois);
            block.endControlFlow();

            return literal(xtrnlzbl);
        }, jokeLub(capturedType, externalizable), ArgumentKind.EXTERNALIZABLE);
    }

    private Strategy getArrayStrategy(ArrayType arrayType) throws CodegenException {
        final TypeMirror component = arrayType.getComponentType();
        final TypeKind componentKind = component.getKind();

        switch (componentKind) {
            case BOOLEAN:
            case INT:
            case SHORT:
            case BYTE:
            case CHAR:
            case LONG:
            case DOUBLE:
            case FLOAT:
                return getPrimitiveArrayStrategy((PrimitiveType) component);
            default:
                final Strategy strategy = getStrategy(component);

                if (strategy != null) {
                    return isSerialStrategy(strategy)
                            ? getSerializableStrategy(types.getArrayType(component))
                            : getSpecialArrayStrategy(strategy, component);
                }
        }

        throw new CodegenException("Unsupported array component type: " + component + ".\n" + getHelpText());
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    private Strategy getBuiltinStrategy(TypeMirror type) throws CodegenException {
        if (sizeF != null && types.isAssignable(type, sizeF)) {
            return newStrategy($ -> literal("$L.readSizeF()", parcelName), sizeF);
        } else if (sizeType != null && types.isAssignable(type, sizeType)) {
            return newStrategy($ -> literal("$L.readSize()", parcelName), sizeType);
        }
        // supported via native methods, always nullable
        else if (types.isAssignable(type, string)) {
            return createNullSafe($ -> literal("$L.readString()", parcelName), string);
        } else if (types.isAssignable(type, iBinder)) {
            return createNullSafe($ -> literal("$L.readStrongBinder()", parcelName), iBinder);
        }
        // supported via non-standard method, always nullable
        else if (types.isAssignable(type, charSequence)) {
            return createNullSafe($ -> literal("$T.CHAR_SEQUENCE_CREATOR.createFromParcel($L)", textUtils, parcelName), charSequence);
        }
        // containers, so naturally nullable
        else if (types.isAssignable(type, sparseBoolArray)) {
            return createNullSafe($ -> literal("$L.readSparseBooleanArray()", parcelName), sparseBoolArray);
        } else if (types.isAssignable(type, bundle)) {
            return createNullSafe($ -> literal("$L.readBundle(getClass().getClassLoader())", parcelName), bundle);
        } else if (types.isAssignable(type, persistable)) {
            return createNullSafe($ -> literal("$L.readPersistableBundle(getClass().getClassLoader())", parcelName), persistable);
        } else {
            if (isEffectivelyObject(type)) {
                if (allowUnchecked) {
                    return createNullSafe($ -> literal("$N.readValue(getClass().getClassLoader())", parcelName), theObject);
                }

                String errMsg =
                        "Passing weakly-typed objects over IPC may result in unsafe code.\n" +
                        "You have two options:\n" +
                        "\t• Use more specific type\n" +
                        "\t• Add @SuppressWarnings(\"unchecked\") annotation to use Parcel#readValue and Parcel#writeValue for transfer";

                if (!types.isSameType(type, theObject)) {
                    errMsg = "It appears, that (in absence of type arguments) type " + type + " boils down to java.lang.Object.\n" + errMsg;
                }

                throw new CodegenException(errMsg);
            }
        }

        return null;
    }

    private @Nullable Strategy getParcelableStrategy(DeclaredType type) throws CodegenException {
        final boolean trulyFinal = type.asElement().getModifiers().contains(Modifier.FINAL);

        final VariableElement creator = lookupStaticField(type, "CREATOR", theCreator);

        if (creator == null) {
            if (trulyFinal) {
                throw new CodegenException("Parcelable type does not have CREATOR field");
            } else {
                getMessager().printMessage(Diagnostic.Kind.WARNING, "Type " + type + " implements Parcelable, " +
                        "but has no 'CREATOR' field");

                return null;
            }
        }

        if (!trulyFinal && !assumeFinal) {
            return null;
        }

        final TypeMirror rawType = types.erasure(type);

        final DeclaredType concreteCreatorType = findDeclaredParent(creator.asType(), theCreator);

        if (concreteCreatorType == null) {
            throw new CodegenException("The type of Parcelable CREATOR can not be determined");
        }

        TypeMirror instantiated = jokeLub(type, parcelable);

        // make special exception for Creator<? super Parcelable>, otherwise let the lack of cast
        // trigger an error to notify user of improper CREATOR declaration
        final List<? extends TypeMirror> creatorOutput = concreteCreatorType.getTypeArguments();
        if (creatorOutput.isEmpty() || !types.isAssignable(creatorOutput.get(0), type)) {
            if (types.isAssignable(concreteCreatorType, genericCreator)) {
                instantiated = parcelable;
            }
        }

        return newStrategy($ -> literal("$T.CREATOR.createFromParcel($N)", rawType, parcelName), instantiated);
    }

    private @NotNull  Strategy getSpecialArrayStrategy(Strategy readingStrategy, TypeMirror actualComponent) {
        if (readingStrategy == VOID_STRATEGY) {
            return VOID_STRATEGY;
        }

        final TypeMirror resultType = makeGenericArray(actualComponent);

        final boolean nullable1 = Util.isNullable(actualComponent, this.nullable);

        final Strategy strategyToUse;

        if (nullable1 && readingStrategy.needNullHandling) {
            strategyToUse = getNullableStrategy(actualComponent, readingStrategy);
        } else {
            strategyToUse = readingStrategy;
        }

        return new SpecialArray(resultType, strategyToUse);
    }

    // Container, so nullable by design
    private final class SpecialArray extends Strategy {
        private final TypeMirror component;
        private final Strategy strategy;

        private SpecialArray(TypeMirror componentType, Strategy componentStrategy) {
            super(null, types.getArrayType(componentType), ArgumentKind.SEQUENCE, false);

            this.component = componentType;
            this.strategy = componentStrategy;
        }

        @Override
        public CodeBlock read(CodeBlock.Builder init) throws CodegenException {
            final String array = allocator.newName(selfName + "Array");
            final String length = allocator.newName(selfName + "Length");
            final String i = allocator.newName("i");

            // TODO: properly propagate nullability both upwards and downwards
            //if (nullable) {
            init.addStatement("final $T[] $N", component, array);
            init.addStatement("final int $N = $N.readInt()", length, parcelName);
            init.beginControlFlow("if ($N < 0)", length);
            init.addStatement("$N = null", array);
            init.nextControlFlow("else");
            init.addStatement("$N = new $L", array, Blocks.arrayInit(types, component, length));
            //} else {
            //    init.addStatement("final $T[] $N = new $L", resultType, array, Blocks.arrayInit(resultType, literal("$N.readInt()", parcelName)));
            //}

            init.beginControlFlow("for (int $N = 0; $N < $N.length; $N++)", i, i, array, i);
            init.addStatement("$N[$N] = $L", array, i, emitCasts(strategy.returnType, component, strategy.read(init)));
            init.endControlFlow();

            //if (nullable1) {
            init.endControlFlow();
            //}

            return literal("$N", array);
        }

        @Override
        public void writeState(DataOutputStream versionCalc) throws IOException {
            super.writeState(versionCalc);

            strategy.writeState(versionCalc);
        }
    }

    private Strategy getMapStrategy(TypeMirror mapType) throws CodegenException {
        // allow upper-bound wildcards to be used
        // not using captureAll() on purpose since any nested types with meaningful type args
        // (e.g. Collections) are going to be handled by recursive application of this method
        final TypeMirror noWildcards = AptHelper.capture(types, mapType);

        final DeclaredType baseMapType = getBaseDeclared(noWildcards, theMap);

        final TypeInvocation<ExecutableElement, ExecutableType> specificPutMethod =
                mapPut.refine(types, baseMapType);

        final TypeMirror keyType = specificPutMethod.type.getParameterTypes().get(0);

        final TypeMirror valueType = specificPutMethod.type.getParameterTypes().get(1);

        Strategy keyStrategy = getStrategy(keyType);

        Strategy valueStrategy = getStrategy(valueType);

        if (types.isAssignable(mapType, serializable)) {
            if (isSerialStrategy(keyStrategy) || isSerialStrategy(valueStrategy)) {
                if (canSerialize(keyType) && canSerialize(valueType)) {
                    return getSerializableStrategy(mapType);
                }
            }

            if ((keyStrategy == null || valueStrategy == null)) {
                if (allowUnchecked) {
                    return getSerializableStrategy(mapType);
                }

                final TypeMirror concreteParent = types.erasure(findConcreteParent(mapType, theCollection));

                throw new CodegenException(
                        "Unable to find serialization strategy for Map type " + mapType + ".\n" +
                        concreteParent + " is serializable, but either key or value is not. If you want" +
                        " Java serialization to be used anyway, add @SuppressWarnings(\"unchecked\") to the method.");
            }
        }

        if (keyStrategy == null || valueStrategy == null) {
            final String errMsg = "Map has unsupported key or value: " + keyType + "/" + valueType + ".\n" + getHelpText();

            throw new CodegenException(errMsg);
        }

        // thankfully, Java does not support multiple inheritance for classes
        final TypeMirror concreteParent = findConcreteParent(mapType, theMap);

        if (concreteParent == null) {
            if (hasBound(mapType, mapBound)) {
                final TypeMirror lt = types.getDeclaredType((TypeElement) hashMap.asElement(), keyType, valueType);

                final TypeMirror t = captureAll(lt);

                return getSimpleMapStrategy(t, t, captureAll(keyType), captureAll(valueType),
                        keyStrategy, valueStrategy, true);
            }

            throw new CodegenException("Unsupported abstract collection type: " + mapType + ". Allowed types: java.util.Map");
        }

        TypeMirror captured = captureAll(mapType);

        if (!Util.isProperDeclared(captured)) {
            throw new IllegalStateException("Type " + captured + " was expected to be classy, but it is not");
        }

        final TypeElement capturedTypeElement = (TypeElement) types.asElement(captured);

        if (!hasPublicDefaultConstructor(capturedTypeElement)) {
            throw new CodegenException("Type " + captured + " does not have default public constructor, can not instantiate");
        }

        TypeMirror newBase = captured;
        TypeMirror newKey = keyType;
        TypeMirror newValue = valueType;

        if (hasPublicMapCapLoadConstructor(capturedTypeElement)) {
            return getCapacityMapStrategy(newBase, captured, newKey, newValue, keyStrategy, valueStrategy, true);
        }

        return getSimpleMapStrategy(newBase, captured, newKey, newValue, keyStrategy, valueStrategy, true);
    }

    private boolean isSerialStrategy(Strategy strategy) {
        return strategy.getClass() == SerializableStrategy.class;
    }

    private Strategy getCollectionStrategy(TypeMirror type) throws CodegenException {
        // allow upper-bound wildcards to be used
        // not using captureAll() on purpose since any nested types with meaningful type args
        // (e.g. Collections) are going to be handled by recursive application of this method
        final TypeMirror noWildcards = AptHelper.capture(types, type);

        final DeclaredType base = getBaseDeclared(noWildcards, theCollection);

        final TypeInvocation<ExecutableElement, ExecutableType> specificAddMethod =
                collectionAdd.refine(types, base);

        final TypeMirror elementType = specificAddMethod.type.getParameterTypes().get(0);

        final boolean elementIsNullable = Util.isNullable(elementType, nullable);

        Strategy elementStrategy = getStrategy(elementType);

        // pretend, that this nonsense didn't happen
        if (elementStrategy == VOID_STRATEGY) {
            return null;
        }

        if (types.isAssignable(type, serializable)) {
            if (elementStrategy == null) {
                if (allowUnchecked) {
                    elementStrategy = getStrategy(theObject);
                } else if (!isFinal(elementType)) {
                    final TypeMirror concreteParent = types.erasure(findConcreteParent(type, theCollection));

                    throw new CodegenException(
                            "Unable to find serialization strategy for Collection type " + type + ".\n"
                            + concreteParent + " is serializable, but it's element is not. If you want"
                            + " Java serialization to be used anyway, add @SuppressWarnings(\"unchecked\") to the method.");
                }
            } else if (isSerialStrategy(elementStrategy)) {
                return getSerializableStrategy(type);
            }
        }

        if (elementStrategy == null) {
            throw new CodegenException("Unsupported collection element type: " + elementType + ".\n" + getHelpText());
        }

        // thankfully, Java does not support multiple inheritance for classes
        final TypeMirror concreteParent = findConcreteParent(type, theCollection);

        if (concreteParent == null) {
            if (hasBound(type, listBound)) {
                final TypeMirror lt = types.getDeclaredType((TypeElement) arrayList.asElement(), elementType);

                final TypeMirror t = captureAll(lt);

                return getCapacityAwareCollectionStrategy(t, t, captureAll(elementType), elementStrategy, elementIsNullable);
            }

            if (hasBound(type, setBound)) {
                final TypeMirror st = types.getDeclaredType((TypeElement) hashSet.asElement(), elementType);

                final TypeMirror t = captureAll(st);

                return getCapacityAwareCollectionStrategy(t, t, captureAll(elementType), elementStrategy, elementIsNullable);
            }

            throw new CodegenException("Unsupported abstract collection type: " + type + ". Allowed types: java.util.List, java.util.Set");
        }

        TypeMirror captured = captureAll(type);

        if (!Util.isProperDeclared(captured)) {
            throw new IllegalStateException("Type " + captured + " was expected to be classy, but it is not");
        }

        final TypeElement capturedTypeElement = (TypeElement) types.asElement(captured);

        if (!hasPublicDefaultConstructor(capturedTypeElement)) {
            throw new CodegenException("Type " + captured + " does not have default public constructor, can not instantiate");
        }

        final TypeMirror newElement;

        TypeMirror newBase = null;

        // if the capture was lossy and resulting type can not be assigned to target type anyway
        // (typical situation when intersections are involved), consider if sacrificing that type
        // may avoid extra cast during element assignment
        if (!types.isAssignable(captured, type)) {
            final TypeInvocation<ExecutableElement, ExecutableType> capturedAddMethod =
                    collectionAdd.refine(types, (DeclaredType) captured);

            final TypeMirror capturedAdd = capturedAddMethod.type.getParameterTypes().get(0);

            if (!types.isAssignable(elementStrategy.returnType, capturedAdd)) {
                // yay! extra cast avoided!
                newElement = captureAll(elementStrategy.returnType);

                final List<? extends TypeParameterElement> params = capturedTypeElement.getTypeParameters();

                // nested types shall not pass
                if (params.size() == 1 && capturedTypeElement.getNestingKind().ordinal() <= 1) {
                    final DeclaredType test = (DeclaredType) capturedTypeElement.asType();

                    final TypeMirror testTypeArg = collectionAdd.refine(types, test)
                            .type.getParameterTypes().get(0);

                    if (types.isSameType(testTypeArg, test.getTypeArguments().get(0))) {
                        newBase = types.getDeclaredType(capturedTypeElement, newElement);
                    }
                }

                if (newBase == null) {
                    newBase = types.getDeclaredType(collectionElement, newElement);
                }
            } else {
                newElement = elementType;
            }
        } else {
            newElement = elementType;
        }

        if (newBase == null) {
            newBase = captured;
        }

        if (hasPublicCapacityConstructor(capturedTypeElement)) {
            return getCapacityAwareCollectionStrategy(newBase, captured, newElement, elementStrategy, elementIsNullable);
        }

        return getSimpleCollectionStrategy(newBase, captured, newElement, elementStrategy, elementIsNullable);
    }

    private boolean hasPublicMapCapLoadConstructor(TypeElement clazz) {
        for (ExecutableElement constructor : ElementFilter.constructorsIn(clazz.getEnclosedElements())) {
            if (constructor.getParameters().size() == 2
                    && constructor.getModifiers().contains(Modifier.PUBLIC)) {
                VariableElement firstParam = constructor.getParameters().get(0);

                if (!types.isAssignable(firstParam.asType(), intType)) {
                    return false;
                }

                VariableElement secondParam = constructor.getParameters().get(1);

                if (!types.isAssignable(secondParam.asType(), floatType)) {
                    return false;
                }

                final Name packageName = elements.getPackageOf(clazz).getQualifiedName();

                if (packageName.toString().startsWith("java.util")) {
                    // I, for one, believe in our Su~~ Oracle overlords
                    return true;
                }

                if (firstParam.getSimpleName().toString().contains("capacity") &&
                        firstParam.getSimpleName().toString().contains("load")) {
                    return true;
                }

                // user's with fully-optimized JDK builds are out of luck :(
            }
        }

        return false;
    }

    private boolean hasPublicCapacityConstructor(TypeElement clazz) {
        for (ExecutableElement constructor : ElementFilter.constructorsIn(clazz.getEnclosedElements())) {
            if (constructor.getParameters().size() == 1
                    && constructor.getModifiers().contains(Modifier.PUBLIC)) {
                VariableElement param = constructor.getParameters().get(0);

                if (types.isAssignable(param.asType(), intType)) {
                    final Name packageName = elements.getPackageOf(clazz).getQualifiedName();

                    // user's with fully-optimized JDK builds are out of luck :(
                    return packageName.toString().startsWith("java.util")
                            || param.getSimpleName().toString().contains("capacity");
                }
            }
        }

        return false;
    }

    private Strategy getSimpleMapStrategy(
            TypeMirror base,
            TypeMirror captured,
            TypeMirror keyType,
            TypeMirror valueType,
            Strategy keyStrategy,
            Strategy valueStrategy,
            boolean elementIsNullable) {
        final Strategy keyStrategyToUse;

        if (elementIsNullable && keyStrategy.needNullHandling) {
            keyStrategyToUse = getNullableStrategy(keyType, keyStrategy);
        } else {
            keyStrategyToUse = keyStrategy;
        }

        final Strategy valueStrategyToUse;

        if (elementIsNullable && valueStrategy.needNullHandling) {
            valueStrategyToUse = getNullableStrategy(valueType, valueStrategy);
        } else {
            valueStrategyToUse = valueStrategy;
        }

        return new SimpleMap(base, captured, keyType, valueType, keyStrategyToUse, valueStrategyToUse);
    }

    private class SimpleMap extends Strategy {
        final TypeMirror captured;
        final TypeMirror keyType;
        final TypeMirror valueType;
        final Strategy keyStrategy;
        final Strategy valueStrategy;

        private SimpleMap(TypeMirror type, TypeMirror captured,
                          TypeMirror keyType, TypeMirror valueType,
                          Strategy keyStrategy, Strategy valueStrategy) {
            super(null, type, ArgumentKind.MAP, false);

            this.captured = captured;
            this.keyType = keyType;
            this.valueType = valueType;
            this.keyStrategy = keyStrategy;
            this.valueStrategy = valueStrategy;
        }

        @Override
        public CodeBlock read(CodeBlock.Builder init) throws CodegenException {
            final String collection = allocator.newName(selfName + "Map");
            final String size = allocator.newName(selfName + "Size");
            final String k = allocator.newName("k");

            init.addStatement("final $T $N", returnType, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitDefaultConstructorCall(captured));

            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", k, k, size, k);
            init.addStatement("$N.put($L, $L)", collection,
                    emitCasts(keyStrategy.returnType, captureAll(keyType), keyStrategy.read(init)),
                    emitCasts(valueStrategy.returnType, captureAll(valueType), valueStrategy.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        }

        @Override
        public void writeState(DataOutputStream versionCalc) throws IOException {
            super.writeState(versionCalc);

            keyStrategy.writeState(versionCalc);
            valueStrategy.writeState(versionCalc);
        }
    }

    private Strategy getCapacityMapStrategy(
            TypeMirror base,
            TypeMirror captured,
            TypeMirror keyType,
            TypeMirror valueType,
            Strategy keyStrategy,
            Strategy valueStrategy,
            boolean elementIsNullable) {
        final Strategy keyStrategyToUse;

        if (elementIsNullable && keyStrategy.needNullHandling) {
            keyStrategyToUse = getNullableStrategy(keyType, keyStrategy);
        } else {
            keyStrategyToUse = keyStrategy;
        }

        final Strategy valueStrategyToUse;

        if (elementIsNullable && valueStrategy.needNullHandling) {
            valueStrategyToUse = getNullableStrategy(valueType, valueStrategy);
        } else {
            valueStrategyToUse = valueStrategy;
        }

        return new CapacityMap(base, captured, keyType, valueType, keyStrategyToUse, valueStrategyToUse);
    }

    private class CapacityMap extends SimpleMap {
        private CapacityMap(TypeMirror type, TypeMirror captured,
                            TypeMirror keyType, TypeMirror valueType,
                            Strategy keyStrategy, Strategy valueStrategy) {
            super(type, captured, keyType, valueType, keyStrategy, valueStrategy);
        }

        @Override
        public CodeBlock read(CodeBlock.Builder init) throws CodegenException {
            final String collection = allocator.newName(selfName + "Map");
            final String size = allocator.newName(selfName + "Size");
            final String k = allocator.newName("k");

            init.addStatement("final $T $N", returnType, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitMapConstructorCall(captured, size));

            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", k, k, size, k);
            init.addStatement("$N.put($L, $L)", collection,
                    emitCasts(keyStrategy.returnType, captureAll(keyType), keyStrategy.read(init)),
                    emitCasts(valueStrategy.returnType, captureAll(valueType), valueStrategy.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        };
    }

    private Strategy getSimpleCollectionStrategy(
            TypeMirror base,
            TypeMirror captured,
            TypeMirror elementType,
            Strategy strategy,
            boolean elementIsNullable) {
        Strategy strategyToUse;

        if (elementIsNullable && strategy.needNullHandling) {
            strategyToUse = getNullableStrategy(elementType, strategy);
        } else {
            strategyToUse = strategy;
        }

        return new SimpleCollection(base, elementType, captured, strategyToUse);
    }

    private class SimpleCollection extends Strategy {
        final TypeMirror elementType;

        final TypeMirror captured;
        final Strategy strategy;

        private SimpleCollection(TypeMirror type, TypeMirror elementType,
                                 TypeMirror captured, Strategy elementStrategy) {
            super(null, type, ArgumentKind.SEQUENCE, false);

            this.elementType = elementType;

            this.captured = captured;
            this.strategy = elementStrategy;
        }

        @Override
        public CodeBlock read(CodeBlock.Builder init) throws CodegenException {
            final String collection = allocator.newName(selfName + "Collection");
            final String size = allocator.newName(selfName + "Size");
            final String i = allocator.newName("j");

            init.addStatement("final $T $N", returnType, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitDefaultConstructorCall(captured));
            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", i, i, size, i);
            init.addStatement("$N.add($L)", collection, emitCasts(strategy.returnType, captureAll(elementType), strategy.read(init)));
            init.endControlFlow();
            init.endControlFlow();

            return literal(collection);
        }

        @Override
        public void writeState(DataOutputStream versionCalc) throws IOException {
            super.writeState(versionCalc);

            strategy.writeState(versionCalc);
        }
    }

    private Strategy getCapacityAwareCollectionStrategy(
            TypeMirror base,
            TypeMirror captured,
            TypeMirror elementType,
            Strategy strategy,
            boolean elementIsNullable)
    {
        final Strategy strategyToUse;

        if (elementIsNullable && strategy.needNullHandling) {
            strategyToUse = getNullableStrategy(elementType, strategy);
        } else {
            strategyToUse = strategy;
        }

        return new CapacityAwareCollection(base, elementType, captured, strategyToUse);
    }

    // Always nullable by design
    private final class CapacityAwareCollection extends SimpleCollection {
        private CapacityAwareCollection(TypeMirror type, TypeMirror elementType,
                                        TypeMirror captured, Strategy elementStrategy) {
            super(type, elementType, captured, elementStrategy);
        }

        @Override
        public CodeBlock read(CodeBlock.Builder init) throws CodegenException {
            final String collection = allocator.newName(selfName + "Collection");
            final String size = allocator.newName(selfName + "Size");
            final String i = allocator.newName("j");

            init.addStatement("final $T $N", returnType, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitCapacityConstructorCall(captured, size));
            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", i, i, size, i);
            init.addStatement("$N.add($L)", collection, emitCasts(strategy.returnType, captureAll(elementType), strategy.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        }
    }

    private Strategy getSerializableStrategy(TypeMirror type) {
        return new SerializableStrategy(jokeLub(type, serializable));
    }

    private Strategy getUnknownExternalizableStrategy(TypeMirror type) {
        return new SerializableStrategy(jokeLub(type, externalizable));
    }

    private Strategy getUnknownParcelableStrategy(TypeMirror type) {
        return new BaseParcelableStrategy(jokeLub(type, parcelable));
    }

    // Always nullable by design
    private final class SerializableStrategy extends Strategy {
        private final CodeBlock block = literal("$T.readFromObjectStream($N)", AidlUtil.class, parcelName);

        private SerializableStrategy(TypeMirror returnTypeRefined) {
            super(null, returnTypeRefined, ArgumentKind.SERIALIZABLE, false);
        }

        @Override
        public CodeBlock read(CodeBlock.Builder unused) {
            return block;
        }
    }

    // Always nullable by design
    private final class BaseParcelableStrategy extends Strategy {
        private final CodeBlock block = literal("$L.readParcelable(getClass().getClassLoader())", parcelName);

        private BaseParcelableStrategy(TypeMirror returnTypeRefined) {
            super(null, returnTypeRefined, ArgumentKind.PARCELABLE, false);
        }

        @Override
        public CodeBlock read(CodeBlock.Builder unused) {
            return block;
        }
    }

    // Container, so nullable by design
    private Strategy getPrimitiveArrayStrategy(PrimitiveType component) {
        final TypeKind componentKind = component.getKind();

        final CodeBlock block;

        switch (componentKind) {
            case BYTE:
                block = literal("$N.createByteArray()", parcelName);
                break;
            case INT:
                block = literal("$N.createIntArray()", parcelName);
                break;
            case BOOLEAN:
                block = literal("$N.createBooleanArray()", parcelName);
                break;
            case CHAR:
                block = literal("$N.createCharArray()", parcelName);
                break;
            case LONG:
                block = literal("$N.createLongArray()", parcelName);
                break;
            case DOUBLE:
                block = literal("$N.createDoubleArray()", parcelName);
                break;
            case FLOAT:
                block = literal("$N.createFloatArray()", parcelName);
                break;
            default:
                return getSerializableStrategy(types.getArrayType(component));
        }

        return new PrimitiveArrayStrategy(init -> block, types.getArrayType(component));
    }

    private final class PrimitiveArrayStrategy extends Strategy {
        private final TypeKind kind;

        private PrimitiveArrayStrategy(ReadingStrategy delegate, TypeMirror type) {
            super(delegate, type, ArgumentKind.AUTO, false);

            this.kind = type.getKind();
        }

        @Override
        public void writeState(DataOutputStream versionCalc) throws IOException {
            super.writeState(versionCalc);

            versionCalc.write(PRIMITIVE_ARRAY);
            versionCalc.write(kind.ordinal());
        }
    }

    private CodeBlock readPrimitive(PrimitiveType type) {
        switch (type.getKind()) {
            case LONG:
                return literal("$N.readLong()", parcelName);
            case DOUBLE:
                return literal("$N.readDouble()", parcelName);
            case FLOAT:
                return literal("$N.readFloat()", parcelName);
            case BOOLEAN:
                return literal("$N.readInt() == 1", parcelName);
            case INT:
                return literal("$N.readInt()", parcelName);
            default:
                return literal("($T) $N.readInt()", type, parcelName);
        }
    }

    private interface ReadingStrategy {
        CodeBlock read(CodeBlock.Builder block) throws CodegenException;
    }

    private class Strategy implements ReadingStrategy {
        private final ReadingStrategy delegate;
        private final boolean needNullHandling;
        private final ArgumentKind kind;

        final TypeMirror returnType;

        private Strategy(ReadingStrategy delegate, TypeMirror returnTypeRefined, ArgumentKind kind, boolean needNullHandling) {
            this.delegate = delegate;
            this.needNullHandling = needNullHandling;
            this.kind = kind;

            this.returnType = returnTypeRefined;
        }

        public void writeState(DataOutputStream versionCalc) throws IOException {
            versionCalc.writeInt("returnValue".equals(selfName) ? RET_VAL : INPUT_ARGUMENT);
            versionCalc.writeInt(kind.ordinal());
        }

        @Override
        public CodeBlock read(CodeBlock.Builder block) throws CodegenException {
            return delegate.read(block);
        }
    }

    public Strategy createNullSafe(ReadingStrategy delegate, TypeMirror returnType, ArgumentKind kind) {
        return new Strategy(delegate, returnType, kind, false);
    }

    public Strategy newStrategy(ReadingStrategy delegate, TypeMirror returnType, ArgumentKind kind) {
        return new Strategy(delegate, returnType, kind, true);
    }

    public Strategy newStrategy(ReadingStrategy delegate, TypeMirror returnType) {
        return genericAuto(delegate, returnType, true);
    }

    public Strategy createNullSafe(ReadingStrategy delegate, TypeMirror returnType) {
        return genericAuto(delegate, returnType, false);
    }

    private Strategy genericAuto(ReadingStrategy delegate, TypeMirror returnType, boolean nullSafe) {
        return new Strategy(delegate, returnType, ArgumentKind.AUTO, nullSafe) {
            @Override
            public void writeState(DataOutputStream versionCalc) throws IOException {
                super.writeState(versionCalc);

                if (returnType.getKind().isPrimitive()) {
                    versionCalc.writeInt(PRIMITIVE);
                    versionCalc.writeInt(returnType.getKind().ordinal());
                } else {
                    final Name qualified = getQualifiedName((DeclaredType) returnType);
                    versionCalc.writeInt(TYPE_NAME);
                    versionCalc.writeUTF(qualified == null ? returnType.toString() : qualified.toString());
                    versionCalc.writeByte(0);
                }
            }
        };
    }
}
