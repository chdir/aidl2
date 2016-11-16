package net.sf.aidl2.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.NameAllocator;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.InterfaceLoader;
import net.sf.aidl2.internal.codegen.Blocks;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.codegen.TypedExpression;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import static net.sf.aidl2.internal.util.Util.*;

public final class Reader extends AptHelper {
    private final ClassName textUtils = ClassName.get("android.text", "TextUtils");

    private final boolean allowUnchecked;
    private final boolean external;
    private final boolean nullable;
    private final boolean assumeFinal;

    private final DeclaredType parcelable;
    private final DeclaredType externalizable;

    private final TypeMirror sizeF;
    private final TypeMirror sizeType;
    private final TypeMirror iBinder;
    private final TypeMirror bundle;
    private final TypeMirror persistable;
    private final TypeMirror sparseBoolArray;

    private final TypeMirror string;
    private final TypeMirror serializable;
    private final TypeMirror charSequence;

    private final TypeMirror intType;
    private final TypeMirror floatType;

    protected final DeclaredType genericCreator;

    private final CharSequence parcelName;
    private final CharSequence selfName;
    private final NameAllocator allocator;

    private final TypeInvocation<ExecutableElement, ExecutableType> collectionAdd;
    private final TypeInvocation<ExecutableElement, ExecutableType> mapPut;

    public Reader(AidlProcessor.Environment environment, State state, CharSequence parcelName) {
        super(environment);

        this.parcelName = parcelName;

        this.selfName = state.name;
        this.allowUnchecked = state.allowUnchecked;
        this.nullable = state.nullable;
        this.external = state.external;
        this.allocator = state.allocator;
        this.assumeFinal = state.assumeFinal;

        this.sizeType = lookup("android.util.Size");
        this.sizeF = lookup("android.util.SizeF");
        this.parcelable = lookup("android.os.Parcelable");
        this.iBinder = lookup("android.os.IBinder");
        this.bundle = lookup("android.os.Bundle");
        this.persistable = lookup("android.os.PersistableBundle");
        this.sparseBoolArray = lookup("android.util.SparseBooleanArray");

        this.string = lookup(String.class);
        this.serializable = lookup(Serializable.class);
        this.charSequence = lookup(CharSequence.class);
        this.externalizable = lookup(Externalizable.class);

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
    public TypedExpression read(CodeBlock.Builder block, TypeMirror type) throws CodegenException {
        final Strategy strategy = getStrategy(type);

        if (strategy != null) {
            final CodeBlock assignment;

            if (nullable && strategy.needNullHandling) {
                assignment = getNullableStrategy(type, strategy).read(block);
            } else {
                assignment = strategy.read(block);
            }

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

        return Strategy.createNullSafe(init -> {
            final String tmp = allocator.newName(selfName + "Tmp");

            init.addStatement("final $T $N", capturedType, tmp);
            init.beginControlFlow("if ($N.readByte() == -1)", parcelName);
            init.addStatement("$N = null", tmp);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", tmp, emitCasts(inner.returnType, capturedType, inner.read(init)));
            init.endControlFlow();

            return literal(tmp);
        }, type);
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
                return Strategy.createNullSafe($ -> readPrimitive((PrimitiveType) type), type);
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

    private final Strategy VOID_STRATEGY = Strategy.createNullSafe($ -> literal("null"), types.getNullType());

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
                        return Strategy.create($ -> readPrimitive(primitiveVariety), primitiveVariety);
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
        return Strategy.createNullSafe(block -> literal("$T.asInterface($N.readStrongBinder())", stubClass, parcelName),
                stubClass.getEnclosingElement().asType());
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

            return Strategy.createNullSafe(strategy, jokeLub(captured, theIInterface));
        } else {
            strategy = block -> literal("$T.asInterface($N.readStrongBinder(), $T.class)",
                    InterfaceLoader.class, parcelName, raw);

            return Strategy.createNullSafe(strategy, jokeLub(type, theIInterface));
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

        return Strategy.create(block -> {
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
        }, jokeLub(capturedType, externalizable));
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
        if (types.isAssignable(type, sizeF)) {
            return Strategy.create($ -> literal("$L.readSizeF()", parcelName), sizeF);
        } else if (types.isAssignable(type, sizeType)) {
            return Strategy.create($ -> literal("$L.readSize()", parcelName), sizeType);
        }
        // supported via native methods, always nullable
        else if (types.isAssignable(type, string)) {
            return Strategy.createNullSafe($ -> literal("$L.readString()", parcelName), string);
        } else if (types.isAssignable(type, iBinder)) {
            return Strategy.createNullSafe($ -> literal("$L.readStrongBinder()", parcelName), iBinder);
        }
        // supported via non-standard method, always nullable
        else if (types.isAssignable(type, charSequence)) {
            return Strategy.createNullSafe($ -> literal("$T.CHAR_SEQUENCE_CREATOR.createFromParcel($L)", textUtils, parcelName), charSequence);
        }
        // containers, so naturally nullable
        else if (types.isAssignable(type, sparseBoolArray)) {
            return Strategy.createNullSafe($ -> literal("$L.readSparseBooleanArray()", parcelName), sparseBoolArray);
        } else if (types.isAssignable(type, bundle)) {
            return Strategy.createNullSafe($ -> literal("$L.readBundle(getClass().getClassLoader())", parcelName), bundle);
        } else if (types.isAssignable(type, persistable)) {
            return Strategy.createNullSafe($ -> literal("$L.readPersistableBundle(getClass().getClassLoader())", parcelName), persistable);
        } else {
            if (isEffectivelyObject(type)) {
                if (allowUnchecked) {
                    return Strategy.createNullSafe($ -> literal("$N.readValue(getClass().getClassLoader())", parcelName), theObject);
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

        return Strategy.create($ -> literal("$T.CREATOR.createFromParcel($N)", rawType, parcelName), instantiated);
    }

    // Container, so nullable by design
    private @NotNull  Strategy getSpecialArrayStrategy(Strategy readingStrategy, TypeMirror actualComponent) {
        if (readingStrategy == VOID_STRATEGY) {
            return VOID_STRATEGY;
        }

        final TypeMirror resultType = makeGenericArray(actualComponent);

        // required to determine whether we need to make a cast
        final TypeMirror returnedType = readingStrategy.returnType;

        return Strategy.createNullSafe(init -> {
            final String array = allocator.newName(selfName + "Array");
            final String length = allocator.newName(selfName + "Length");
            final String i = allocator.newName("i");

            // TODO: properly propagate nullability both upwards and downwards
            //if (nullable) {
            init.addStatement("final $T[] $N", resultType, array);
            init.addStatement("final int $N = $N.readInt()", length, parcelName);
            init.beginControlFlow("if ($N < 0)", length);
            init.addStatement("$N = null", array);
            init.nextControlFlow("else");
            init.addStatement("$N = new $L", array, Blocks.arrayInit(types, resultType, length));
            //} else {
            //    init.addStatement("final $T[] $N = new $L", resultType, array, Blocks.arrayInit(resultType, literal("$N.readInt()", parcelName)));
            //}

            final boolean nullable1 = Util.isNullable(actualComponent, this.nullable);

            final Strategy strategyToUse;

            if (nullable1 && readingStrategy.needNullHandling) {
                strategyToUse = getNullableStrategy(actualComponent, readingStrategy);
            } else {
                strategyToUse = readingStrategy;
            }

            init.beginControlFlow("for (int $N = 0; $N < $N.length; $N++)", i, i, array, i);
            init.addStatement("$N[$N] = $L", array, i, emitCasts(returnedType, resultType, strategyToUse.read(init)));
            init.endControlFlow();

            //if (nullable1) {
            init.endControlFlow();
            //}

            return literal("$N", array);
        }, types.getArrayType(resultType));
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
            if (!allowUnchecked && (keyStrategy == null || valueStrategy == null)) {
                final TypeMirror concreteParent = types.erasure(findConcreteParent(mapType, theCollection));

                throw new CodegenException(
                        "Unable to find serialization strategy for Map type " + mapType + ".\n"
                        + concreteParent + " is serializable, but either key or value is not. If you want"
                        + " Java serialization to be used anyway, add @SuppressWarnings(\"unchecked\") to the method.");
            }

            if (keyStrategy == null) {
                keyStrategy = getStrategy(theObject);
            }

            if (valueStrategy == null) {
                valueStrategy = getStrategy(theObject);
            }

            if (isSerialStrategy(keyStrategy) && isSerialStrategy(valueStrategy)) {
                return getSerializableStrategy(mapType);
            }
        }

        if (keyStrategy == null || valueStrategy == null) {
            final String errMsg = "Unsupported map key/value combination: " + keyType + "/" + valueType + ".\n" + getHelpText();

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
        return strategy.getClass() == ExternalizableStrategy.class ||
                strategy.getClass() == SerializableStrategy.class;
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
        return Strategy.createNullSafe(init -> {
            final String collection = allocator.newName(selfName + "Map");
            final String size = allocator.newName(selfName + "Size");
            final String k = allocator.newName("k");

            init.addStatement("final $T $N", base, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitDefaultConstructorCall(captured));

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

            final TypeMirror keyResultType = keyStrategy.returnType;
            final TypeMirror valueResultType = valueStrategy.returnType;

            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", k, k, size, k);
            init.addStatement("$N.put($L, $L)", collection,
                    emitCasts(keyResultType, captureAll(keyType), keyStrategyToUse.read(init)),
                    emitCasts(valueResultType, captureAll(valueType), valueStrategyToUse.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        }, base);
    }

    private Strategy getCapacityMapStrategy(
            TypeMirror base,
            TypeMirror captured,
            TypeMirror keyType,
            TypeMirror valueType,
            Strategy keyStrategy,
            Strategy valueStrategy,
            boolean elementIsNullable) {
        return Strategy.createNullSafe(init -> {
            final String collection = allocator.newName(selfName + "Map");
            final String size = allocator.newName(selfName + "Size");
            final String k = allocator.newName("k");

            init.addStatement("final $T $N", base, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitMapConstructorCall(captured, size));

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

            final TypeMirror keyResultType = keyStrategy.returnType;
            final TypeMirror valueResultType = valueStrategy.returnType;

            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", k, k, size, k);
            init.addStatement("$N.put($L, $L)", collection,
                    emitCasts(keyResultType, captureAll(keyType), keyStrategyToUse.read(init)),
                    emitCasts(valueResultType, captureAll(valueType), valueStrategyToUse.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        }, base);
    }

    private Strategy getSimpleCollectionStrategy(
            TypeMirror base,
            TypeMirror captured,
            TypeMirror elementType,
            Strategy strategy,
            boolean elementIsNullable) {
        return Strategy.createNullSafe(init -> {
            final String collection = allocator.newName(selfName + "Collection");
            final String size = allocator.newName(selfName + "Size");
            final String i = allocator.newName("j");

            init.addStatement("final $T $N", base, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitDefaultConstructorCall(captured));

            final Strategy strategyToUse;

            if (elementIsNullable && strategy.needNullHandling) {
                strategyToUse = getNullableStrategy(elementType, strategy);
            } else {
                strategyToUse = strategy;
            }

            final TypeMirror resultType = strategy.returnType;

            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", i, i, size, i);
            init.addStatement("$N.add($L)", collection, emitCasts(resultType, captureAll(elementType), strategyToUse.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        }, base);
    }

    private Strategy getCapacityAwareCollectionStrategy(
            TypeMirror base,
            TypeMirror captured,
            TypeMirror elementType,
            Strategy strategy,
            boolean elementIsNullable) {
        return Strategy.createNullSafe(init -> {
            final String collection = allocator.newName(selfName + "Collection");
            final String size = allocator.newName(selfName + "Size");
            final String i = allocator.newName("j");

            init.addStatement("final $T $N", base, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitCapacityConstructorCall(captured, size));

            final Strategy strategyToUse;

            if (elementIsNullable && strategy.needNullHandling) {
                strategyToUse = getNullableStrategy(elementType, strategy);
            } else {
                strategyToUse = strategy;
            }

            final TypeMirror resultType = strategy.returnType;

            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", i, i, size, i);
            init.addStatement("$N.add($L)", collection, emitCasts(resultType, captureAll(elementType), strategyToUse.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        }, base);
    }

    private Strategy getSerializableStrategy(TypeMirror type) {
        return new SerializableStrategy(jokeLub(type, serializable));
    }

    // Always nullable by design
    private final class SerializableStrategy extends Strategy {
        private final CodeBlock block = readSerializable();

        private SerializableStrategy(TypeMirror returnTypeRefined) {
            super(null, returnTypeRefined, false);
        }

        @Override
        public CodeBlock read(CodeBlock.Builder unused) {
            return block;
        }
    }

    private Strategy getUnknownExternalizableStrategy(TypeMirror type) {
        return new ExternalizableStrategy(jokeLub(type, externalizable));
    }

    // Always nullable by design
    private final class ExternalizableStrategy extends Strategy {
        private final CodeBlock block = literal("$T.readSafeExternalizable($N)", AidlUtil.class, parcelName);

        private ExternalizableStrategy(TypeMirror returnTypeRefined) {
            super(null, returnTypeRefined, false);
        }

        @Override
        public CodeBlock read(CodeBlock.Builder unused) {
            return block;
        }
    }

    // Always nullable by design
    private Strategy UNKNOWN_PARCELABLE_STRATEGY;

    private Strategy getUnknownParcelableStrategy(TypeMirror type) {
        if (UNKNOWN_PARCELABLE_STRATEGY == null) {
            UNKNOWN_PARCELABLE_STRATEGY = Strategy.createNullSafe(new ReadingStrategy() {
                private final CodeBlock block = literal("$L.readParcelable(getClass().getClassLoader())", parcelName);

                @Override
                public CodeBlock read(CodeBlock.Builder unused) {
                    return block;
                }
            }, jokeLub(type, parcelable));
        }

        return UNKNOWN_PARCELABLE_STRATEGY;
    }

    // Container, so nullable by design
    private Strategy getPrimitiveArrayStrategy(PrimitiveType component) {
        return Strategy.createNullSafe(block -> {
            final TypeKind componentKind = component.getKind();

            switch (componentKind) {
                case BYTE:
                    return literal("$N.createByteArray()", parcelName);
                case INT:
                    return literal("$N.createIntArray()", parcelName);
                case BOOLEAN:
                    return literal("$N.createBooleanArray()", parcelName);
                case CHAR:
                    return literal("$N.createCharArray()", parcelName);
                case LONG:
                    return literal("$N.createLongArray()", parcelName);
                case DOUBLE:
                    return literal("$N.createDoubleArray()", parcelName);
                case FLOAT:
                    return literal("$N.createFloatArray()", parcelName);
                default:
                    return readSerializable();
            }
        }, types.getArrayType(component));
    }

    private CodeBlock readSerializable() {
        return literal("$T.readSafeSerializable($N)", AidlUtil.class, parcelName);
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

    private static class Strategy implements ReadingStrategy {
        private final ReadingStrategy delegate;
        private final boolean needNullHandling;
        private final TypeMirror returnType;

        private Strategy(ReadingStrategy delegate, TypeMirror returnTypeRefined, boolean needNullHandling) {
            this.delegate = delegate;
            this.returnType = returnTypeRefined;
            this.needNullHandling = needNullHandling;
        }

        public static Strategy create(ReadingStrategy delegate) {
            return new Strategy(delegate, null, true);
        }

        public static Strategy create(ReadingStrategy delegate, TypeMirror returnType) {
            return new Strategy(delegate, returnType, true);
        }

        public static Strategy createNullSafe(ReadingStrategy delegate, TypeMirror returnType) {
            return new Strategy(delegate, returnType, false);
        }

        @Override
        public CodeBlock read(CodeBlock.Builder block) throws CodegenException {
            return delegate.read(block);
        }
    }
}
