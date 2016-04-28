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
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import static net.sf.aidl2.internal.util.Util.getQualifiedName;
import static net.sf.aidl2.internal.util.Util.hasDefaultConstructor;
import static net.sf.aidl2.internal.util.Util.hasPublicDefaultConstructor;
import static net.sf.aidl2.internal.util.Util.literal;

public final class Reader extends AptHelper {
    private final ClassName textUtils = ClassName.get("android.text", "TextUtils");

    private final boolean allowUnchecked;
    private final boolean external;
    private final boolean nullable;

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

    protected final DeclaredType genericCreator;

    private final CharSequence parcelName;
    private final CharSequence selfName;
    private final NameAllocator allocator;

    private final TypeInvocation<ExecutableElement, ExecutableType> collectionAdd;

    public Reader(AidlProcessor.Environment environment, State state, CharSequence parcelName) {
        super(environment);

        this.parcelName = parcelName;

        this.selfName = state.name;
        this.allowUnchecked = state.allowUnchecked;
        this.nullable = state.nullable;
        this.external = state.external;
        this.allocator = state.allocator;

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

        intType = types.getPrimitiveType(TypeKind.INT);
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

        final String errorMsg =
                "Unsupported type: " + type + ".\n" +
                "Must be one of:\n" +
                "\t• android.os.Parcelable, android.os.IInterface, java.io.Serializable, java.io.Externalizable\n" +
                "\t• One of types, natively supported by Parcel or one of primitive type wrappers\n" +
                "\t• Collection of supported type with public default constructor";

        throw new CodegenException(errorMsg);
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

                    return getUnknownParcelableStrategy();
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
                    final Strategy strategy = getExternalizableStrategy(type);

                    if (strategy != null) {
                        return strategy;
                    }
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
                        captured, tmpInterface, tmpBinder, ClassName.get(InterfaceLoader.class), tmpBinder, raw);

                return literal(tmpInterface);
            };

            return Strategy.createNullSafe(strategy, captured);
        } else {
            strategy = block -> literal("$T.asInterface($N.readStrongBinder(), $T.class)",
                    ClassName.get(InterfaceLoader.class), parcelName, raw);

            return Strategy.createNullSafe(strategy, type);
        }
    }

    private Strategy getExternalizableStrategy(TypeMirror t) {
        final DeclaredType type = findConcreteParent(t, externalizable);
        if (type == null) {
            return getUnknownExternalizableStrategy(t);
        }

        final TypeElement clazz = (TypeElement) type.asElement();

        if (!hasDefaultConstructor(clazz)) {
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
        }, capturedType);
    }

    private Strategy getArrayStrategy(ArrayType arrayType) throws CodegenException {
        final TypeMirror component = arrayType.getComponentType();
        final TypeKind componentKind = component.getKind();

        switch (componentKind) {
            case ARRAY:
                return getSpecialArrayStrategy(getArrayStrategy((ArrayType) component), component);
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
                if (types.isSubtype(component, parcelable)) {
                    final DeclaredType concreteParent = findConcreteParent(component, parcelable);

                    if (concreteParent != null) {
                        final Strategy strategy = getParcelableStrategy(concreteParent);

                        if (strategy != null) {
                            return getSpecialArrayStrategy(strategy, component);
                        }
                    }

                    return getSpecialArrayStrategy(getUnknownParcelableStrategy(), component);
                }

                final Strategy specialStrategy = getStrategy(component);

                if (specialStrategy != null) {
                    return getSpecialArrayStrategy(specialStrategy, component);
                }
        }

        final String arrayErrorMsg =
                "Unsupported array component type: " + component + ".\n" +
                "Must be one of:\n" +
                "\t• android.os.Parcelable, android.os.IInterface, java.io.Serializable, java.io.Externalizable\n" +
                "\t• One of types, natively supported by Parcel or one of primitive type wrappers\n" +
                "\t• Collection of supported type with public default constructor";

        throw new CodegenException(arrayErrorMsg);
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
                        "Passing unchecked objects over IPC may result in unsafe code.\n" +
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
        final Set<Modifier> modifiers = type.asElement().getModifiers();

        if (!modifiers.contains(Modifier.FINAL)) {
            return null;
        }

        final VariableElement creator = lookupStaticField(type, "CREATOR", theCreator);

        if (creator == null) {
            throw new CodegenException("Parcelable type does not have CREATOR field");
        }

        final TypeMirror rawType = types.erasure(type);

        final DeclaredType concreteCreatorType = findDeclaredParent(creator.asType(), theCreator);

        if (concreteCreatorType == null) {
            throw new CodegenException("The type of Parcelable CREATOR can not be determined");
        }

        TypeMirror instantiated = type;

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

        if (readingStrategy == SERIALIZABLE_STRATEGY) {
            return getSerializableStrategy(types.getArrayType(actualComponent));
        }

        // arrays don't support generics — the only thing, that matters, is a raw runtime type
        final TypeMirror resultType = types.erasure(actualComponent);

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
            init.addStatement("$N = new $L", array, Blocks.arrayInit(resultType, length));
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

    private Strategy getCollectionStrategy(TypeMirror type) throws CodegenException {
        // allow upper-bound wildcards to be used
        final TypeMirror noWildcards = types.capture(type);

        final DeclaredType base = getCollectionInterface(noWildcards);

        final TypeInvocation<ExecutableElement, ExecutableType> specificAddMethod =
                collectionAdd.refine(types, base);

        final TypeMirror elementType = specificAddMethod.type.getParameterTypes().get(0);

        Strategy elementStrategy = getStrategy(elementType);

        // pretend, that this nonsense didn't happen
        if (elementStrategy == VOID_STRATEGY) {
            return null;
        }

        if (types.isAssignable(type, serializable)) {
            if (elementStrategy == null) {
                if (allowUnchecked) {
                    elementStrategy = getStrategy(theObject);
                } else {
                    final TypeMirror concreteParent = types.erasure(findConcreteParent(type, theCollection));

                    throw new CodegenException(
                            "Unable to find serialization strategy for Collection type " + type + ".\n"
                            + concreteParent + " is serializable, but it's element is not. If you want"
                            + " Java serialization to be used anyway, add @SuppressWarnings(\"unchecked\") to the method.");
                }
            } else if (elementStrategy == SERIALIZABLE_STRATEGY) {
                return getSerializableStrategy(type);
            }
        }

        boolean failedElementTypeCheck = elementStrategy == null;

        // thankfully, Java does not support multiple inheritance for classes
        final TypeMirror concreteParent = findConcreteParent(type, theCollection);

        if (concreteParent == null) {
            if (hasBound(type, listBound)) {
                final TypeMirror lt = types.getDeclaredType((TypeElement) arrayList.asElement(), elementType);

                return getCapacityAwareCollectionStrategy(captureAll(lt), captureAll(elementType), elementStrategy);
            }

            if (hasBound(type, setBound)) {
                final TypeMirror st = types.getDeclaredType((TypeElement) hashSet.asElement(), elementType);

                return getCapacityAwareCollectionStrategy(captureAll(st), captureAll(elementType), elementStrategy);
            }

            throw new CodegenException("Unsupported abstract collection type: " + type + ". Allowed types: java.util.List, java.util.Set");
        }

        final TypeMirror captured = captureAll(type);

        if (!Util.isProperDeclared(captured)) {
            throw new IllegalStateException("Type " + captured + " was expected to be classy, but it is not");
        }

        final TypeElement te = (TypeElement) ((DeclaredType) captured).asElement();

        if (!hasPublicDefaultConstructor(te)) {
            throw new CodegenException("Type " + type + " does not have default public constructor, can not instantiate");
        }

        if (failedElementTypeCheck) {
            final String errMsg = "Unsupported collection element type: " + elementType + ".\n" +
                    "Must be one of:\n" +
                    "\t• android.os.Parcelable, android.os.IInterface, java.io.Serializable, java.io.Externalizable\n" +
                    "\t• One of types, natively supported by Parcel or one of primitive type wrappers\n" +
                    "\t• Collection of supported type with public default constructor";

            throw new CodegenException(errMsg);
        }

        if (hasPublicCapacityConstructor(te)) {
            return getCapacityAwareCollectionStrategy(captured, elementType, elementStrategy);
        }

        return getSimpleCollectionStrategy(captured, elementType, elementStrategy);
    }

    private boolean hasPublicCapacityConstructor(TypeElement clazz) {
        for (ExecutableElement constructor : ElementFilter.constructorsIn(clazz.getEnclosedElements())) {
            if (constructor.getParameters().size() == 1
                    && constructor.getModifiers().contains(Modifier.PUBLIC)) {
                VariableElement param = constructor.getParameters().get(0);

                if (types.isAssignable(param.asType(), intType)) {
                    final Name packageName = elements.getPackageOf(clazz).getQualifiedName();

                    return packageName.toString().startsWith("java.util")
                            || "capacity".equalsIgnoreCase(param.getSimpleName().toString());
                }
            }
        }

        return false;
    }

    private Strategy getSimpleCollectionStrategy(TypeMirror captured, TypeMirror elementType, Strategy strategy) {
        return Strategy.createNullSafe(init -> {
            final String collection = allocator.newName(selfName + "Collection");
            final String size = allocator.newName(selfName + "Size");
            final String i = allocator.newName("j");

            init.addStatement("final $T $N", captured, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitDefaultConstructorCall(captured));

            final boolean nullable1 = Util.isNullable(elementType, nullable);

            final Strategy strategyToUse;

            if (nullable1 && strategy.needNullHandling) {
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
        }, captured);
    }

    private Strategy getCapacityAwareCollectionStrategy(
            TypeMirror captured,
            TypeMirror elementType,
            Strategy strategy) {
        return Strategy.createNullSafe(init -> {
            final String collection = allocator.newName(selfName + "Collection");
            final String size = allocator.newName(selfName + "Size");
            final String i = allocator.newName("j");

            init.addStatement("final $T $N", captured, collection);
            init.addStatement("final int $N = $N.readInt()", size, parcelName);
            init.beginControlFlow("if ($N < 0)", size);
            init.addStatement("$N = null", collection);
            init.nextControlFlow("else");
            init.addStatement("$N = $L", collection, emitCapacityConstructorCall(captured, size));

            final boolean nullable1 = Util.isNullable(elementType, nullable);

            final Strategy strategyToUse;

            if (nullable1 && strategy.needNullHandling) {
                strategyToUse = getNullableStrategy(elementType, strategy);
            } else {
                strategyToUse = strategy;
            }

            final TypeMirror resultType = strategy.returnType;

            init.beginControlFlow("for (int $N = 0; $N < $N; $N++)", i, i, size, i);
            init.addStatement("$N.add($L)", collection, emitCasts(resultType, elementType, strategyToUse.read(init)));
            init.endControlFlow();

            init.endControlFlow();

            return literal(collection);
        }, captured);
    }

    // Always nullable by design
    private Strategy SERIALIZABLE_STRATEGY;

    private Strategy getSerializableStrategy(TypeMirror type) {
        SERIALIZABLE_STRATEGY = Strategy.createNullSafe(new ReadingStrategy() {
            private final CodeBlock block = readSerializable();

            @Override
            public CodeBlock read(CodeBlock.Builder unused) {
                return block;
            }
        }, type);

        return SERIALIZABLE_STRATEGY;
    }

    private Strategy EXTERNALIZABLE_STRATEGY;

    // Always nullable by design
    private Strategy getUnknownExternalizableStrategy(TypeMirror type) {
        if (EXTERNALIZABLE_STRATEGY == null) {
            EXTERNALIZABLE_STRATEGY = Strategy.createNullSafe(new ReadingStrategy() {
                private final CodeBlock block = literal("$T.readSafeExternalizable($N)", ClassName.get(AidlUtil.class), parcelName);

                @Override
                public CodeBlock read(CodeBlock.Builder unused) {
                    return block;
                }
            }, type);
        }

        return EXTERNALIZABLE_STRATEGY;
    }

    // Always nullable by design
    private Strategy UNKNOWN_PARCELABLE_STRATEGY;

    private Strategy getUnknownParcelableStrategy() {
        if (UNKNOWN_PARCELABLE_STRATEGY == null) {
            UNKNOWN_PARCELABLE_STRATEGY = Strategy.createNullSafe(new ReadingStrategy() {
                private final CodeBlock block = literal("$L.readParcelable(getClass().getClassLoader())", parcelName);

                @Override
                public CodeBlock read(CodeBlock.Builder unused) {
                    return block;
                }
            }, parcelable);
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
        return literal("$T.readSafeSerializable($N)", ClassName.get(AidlUtil.class), parcelName);
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
