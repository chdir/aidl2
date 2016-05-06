package net.sf.aidl2.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.NameAllocator;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static net.sf.aidl2.internal.util.Util.hasPublicDefaultConstructor;

public final class Writer extends AptHelper {
    private final ClassName textUtils = ClassName.get("android.text", "TextUtils");

    private final CharSequence parcelName;

    private final boolean allowUnchecked;
    private final boolean nullable;

    private final CharSequence name;
    private final NameAllocator allocator;

    private final DeclaredType parcelable;
    private final DeclaredType externalizable;

    private final TypeMirror string;
    private final TypeMirror charSequence;

    private final TypeMirror sizeF;
    private final TypeMirror sizeType;
    private final TypeMirror iBinder;
    private final TypeMirror bundle;
    private final TypeMirror persistable;
    private final TypeMirror sparseBoolArray;

    private final TypeMirror serializable;

    private final Object flags;

    private final DeclaredType theIterator;

    private final TypeInvocation<ExecutableElement, ExecutableType> collectionIterator;

    public Writer(AidlProcessor.Environment environment, State state, CharSequence outParcelName) {
        super(environment);

        this.nullable = state.nullable;
        this.name = state.name;
        this.allocator = state.allocator;

        this.parcelName = outParcelName;

        this.allowUnchecked = state.allowUnchecked;

        this.sizeType = lookup("android.util.Size");
        this.sizeF = lookup("android.util.SizeF");
        this.parcelable = lookup("android.os.Parcelable");
        this.iBinder = lookup("android.os.IBinder");
        this.bundle = lookup("android.os.Bundle");
        this.persistable = lookup("android.os.PersistableBundle");
        this.sparseBoolArray = lookup("android.util.SparseBooleanArray");

        string = lookup(String.class);
        charSequence = lookup(CharSequence.class);

        this.serializable = lookup(Serializable.class);
        this.externalizable = lookup(Externalizable.class);

        this.theIterator = lookup(Iterator.class);

        collectionIterator = lookupMethod(theCollection, "iterator", "Iterator");

        if (state.returnValue) {
            flags = Util.literal("$T.PARCELABLE_WRITE_RETURN_VALUE", parcelable);
        } else {
            flags = 0;
        }
    }

    public void write(CodeBlock.Builder paramWriting, TypeMirror type) throws CodegenException {
        Strategy strategy = getStrategy(type);

        if (strategy != null) {
            if (nullable && strategy.needNullHandling) {
                getNullableStrategy(strategy)
                        .write(paramWriting, name, type);
            } else {
                strategy.write(paramWriting, name, type);
            }

            return;
        }

        final String errorMsg =
                "Unsupported parameter type: " + type + ".\n" +
                "Must be one of:\n" +
                "\t• android.os.Parcelable, android.os.IInterface, java.io.Serializable, java.io.Externalizable\n" +
                "\t• One of types, natively supported by Parcel or one of primitive type wrappers\n" +
                "\t• Collection of supported type with public default constructor";

        throw new CodegenException(errorMsg);
    }

    public TypeMirror writeReturnValue(CodeBlock.Builder retValWriting, TypeMirror type) throws CodegenException {
        Strategy strategy = getStrategy(type);

        if (strategy != null) {
            final TypeMirror capture = captureAll(strategy.requiredType);

            if (nullable && strategy.needNullHandling) {
                getNullableStrategy(strategy)
                        .write(retValWriting, name, capture);
            } else {
                strategy.write(retValWriting, name, capture);
            }

            return capture;
        }

        final String errorMsg =
                "Unsupported return value type: " + type + ".\n" +
                        "Must be one of:\n" +
                        "\t• android.os.Parcelable, android.os.IInterface, java.io.Serializable, java.io.Externalizable\n" +
                        "\t• One of types, natively supported by Parcel or one of primitive type wrappers\n" +
                        "\t• Collection of supported type with public default constructor";

        throw new CodegenException(errorMsg);
    }

    private Strategy getStrategy(TypeMirror type) throws CodegenException {
        switch (type.getKind()) {
            case BOOLEAN:
            case INT:
            case SHORT:
            case BYTE:
            case CHAR:
            case LONG:
            case DOUBLE:
            case FLOAT:
                return Strategy.createNullSafe((b, n, t) -> writePrimitive(b, n, (PrimitiveType) type), type);
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
                    final Strategy strategy = getParcelableStrategy(type);

                    if (strategy != null) {
                        return strategy;
                    }
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
                    return getExternalizableStrategy(type);
                }

                // check for varargs, that resolve to wrapper types...
                final TypeMirror captured = types.erasure(type);

                if (captured.getKind() == TypeKind.DECLARED) {
                    final Strategy typeArgWrapperTypeStr = getWrapperTypeStrategy((DeclaredType) captured);

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
                    return getSerializableStrategy();
                }
        }

        return null;
    }

    private final Strategy VOID_STRATEGY = Strategy.createNullSafe(($, $$, $$$) -> {}, theObject);

    private Strategy getWrapperTypeStrategy(DeclaredType type) {
        final CharSequence name = Util.getQualifiedName(type);
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
                        return Strategy.create((b, n, t) -> writePrimitive(b, n, primitiveVariety), type);
                    }
                    break;
                case "java.lang.Void":
                    return VOID_STRATEGY;
            }
        }

        return null;
    }

    // Always nullable by design
    private Strategy EXTERNALIZABLE_STRATEGY;

    private Strategy getUnknownExternalizableStrategy(TypeMirror t) {
        if (EXTERNALIZABLE_STRATEGY == null) {
            EXTERNALIZABLE_STRATEGY = Strategy.createNullSafe(Writer.this::writeExternalizable, externalizable);
        }

        return EXTERNALIZABLE_STRATEGY;
    }

    // Always nullable by design
    private Strategy SERIALIZABLE_STRATEGY;

    private Strategy getSerializableStrategy() {
        if (SERIALIZABLE_STRATEGY == null) {
            SERIALIZABLE_STRATEGY = Strategy.createNullSafe(Writer.this::writeSerializable, serializable);
        }

        return SERIALIZABLE_STRATEGY;
    }

    private Strategy getIInterfaceStrategy(TypeMirror type) throws CodegenException {
        final DeclaredType declared = findDeclaredParent(type, theIInterface);

        if (declared == null || types.isSameType(declared, theIInterface)) {
            throw new CodegenException("Can not pass unknown android.os.IInterface subtype over IPC. Try to use more specific type.");
        }

        final TypeElement el = (TypeElement) declared.asElement();

        if (Util.getAnnotation(el, AIDL.class) == null) {
            final TypeElement stubClass = lookupStaticClass(declared, "Stub", theBinder);

            if (stubClass != null) {
                if (lookupStaticMethod((DeclaredType) stubClass.asType(), "asInterface", theIInterface, "IBinder") == null) {
                    // the type can not be deserialized as IInterface, so serializing it as such
                    // isn't possible either
                    return null;
                }
            }
        }

        final WritingStrategy strategy = Util.isNullable(type, nullable)
                ? (b, obj, unused) -> b.addStatement("$L.writeStrongBinder($L == null ? null : $L.asBinder())", parcelName, obj, obj)
                : (b, obj, unused) -> b.addStatement("$L.writeStrongBinder($L.asBinder())", parcelName, obj);

        return Strategy.createNullSafe(strategy, type);
    }

    private Strategy getExternalizableStrategy(TypeMirror t) {
        final DeclaredType type = findConcreteParent(t, externalizable);
        if (type == null) {
            return getUnknownExternalizableStrategy(t);
        }

        return Strategy.create((block, name, unused) -> {
            final String oos = allocator.newName("objectOutputStream");
            final String baos = allocator.newName("arrayOutputStream");
            final String err = allocator.newName("e");

            block.addStatement("$T $N = null", ObjectOutputStream.class, oos);

            block.beginControlFlow("try");

            block.addStatement("$T $N = new $T()",
                    ByteArrayOutputStream.class, baos, ByteArrayOutputStream.class);
            block.addStatement("$N = new $T($N)", oos, ObjectOutputStream.class, baos);

            block.addStatement("$L.writeExternal($N)", name, oos);
            block.addStatement("$L.writeByteArray($N.toByteArray())", parcelName, baos);

            block.nextControlFlow("catch (Exception $N)", err);

            block.addStatement("throw new IllegalStateException($S, $N)", "Failed to serialize " + type, err);

            block.nextControlFlow("finally");
            block.addStatement("$T.shut($N)", AidlUtil.class, oos);
            block.endControlFlow();
        }, externalizable);
    }

    private Strategy getParcelableStrategy(TypeMirror type) {
        final DeclaredType concreteType = findConcreteParent(type, parcelable);

        if (concreteType == null) {
            return getAbstractParcelableStrategy();
        }

        final VariableElement creator = lookupStaticField(concreteType, "CREATOR", theCreator);

        if (creator == null) {
            return getAbstractParcelableStrategy();
        }

        final DeclaredType concreteCreatorType = findDeclaredParent(creator.asType(), theCreator);

        if (concreteCreatorType == null) {
            return getAbstractParcelableStrategy();
        }

        return Strategy.create((block, name, unused) -> block.addStatement("$L.writeToParcel($L, $L)", name, parcelName, flags), parcelable);
    }

    private Strategy getAbstractParcelableStrategy() {
        return Strategy.createNullSafe((b, name, unused) -> b.addStatement("$L.writeParcelable($L, $L)", parcelName, name, flags), parcelable);
    }

    private Strategy getBuiltinStrategy(TypeMirror type) throws CodegenException {
        if (types.isAssignable(type, sizeF)) {
            return Strategy.create((block, name, unused) -> block.addStatement("$L.writeSizeF($L)", parcelName, name), sizeF);
        } else if (types.isAssignable(type, sizeType)) {
            return Strategy.create((block, name, unused) -> block.addStatement("$L.writeSize($L)", parcelName, name), sizeType);
        }
        // supported via native methods, always nullable
        else if (types.isAssignable(type, string)) {
            return Strategy.createNullSafe((block, name, unused) -> block.addStatement("$L.writeString($L)", parcelName, name), string);
        } else if (types.isAssignable(type, iBinder)) {
            return Strategy.createNullSafe((block, name, unused) -> block.addStatement("$L.writeStrongBinder($L)", parcelName, name), iBinder);
        }
        // supported via non-standard method, always nullable
        else if (types.isAssignable(type, charSequence)) {
            return Strategy.createNullSafe((block, name, unused) -> block.addStatement("$T.writeToParcel($L, $L, $L)", textUtils, name, parcelName, flags), charSequence);
        }
        // containers, so naturally nullable
        else if (types.isAssignable(type, sparseBoolArray)) {
            return Strategy.createNullSafe((block, name, unused) -> block.addStatement("$L.writeSparseBooleanArray($L)", parcelName, name), sparseBoolArray);
        } else if (types.isAssignable(type, bundle)) {
            return Strategy.createNullSafe((block, name, unused) -> block.addStatement("$L.writeBundle($L)", parcelName, name), bundle);
        } else if (types.isAssignable(type, persistable)) {
            return Strategy.createNullSafe((block, name, unused) -> block.addStatement("$L.writePersistableBundle($L)", parcelName, name), persistable);
        } else {
            if (isEffectivelyObject(type)) {
                if (allowUnchecked) {
                    return Strategy.createNullSafe((block, name, unused) -> block.addStatement("$N.writeValue($L)", parcelName, name), theObject);
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

    private Strategy getNullableStrategy(Strategy strategy) {
        if (strategy == VOID_STRATEGY) {
            return strategy;
        }

        return Strategy.createNullSafe((block, name, unused) -> {
            block.beginControlFlow("if ($L == null)", name);
            block.addStatement("$N.writeByte((byte) -1)", parcelName);
            block.nextControlFlow("else");
            block.addStatement("$N.writeByte((byte) 0)", parcelName);
            strategy.write(block, name, strategy.requiredType);
            block.endControlFlow();
        }, strategy.requiredType);
    }

    private Strategy getArrayStrategy(ArrayType arrayType) throws CodegenException {
        final TypeMirror componentType = arrayType.getComponentType();
        final TypeKind componentKind = componentType.getKind();

        switch (componentKind) {
            case ARRAY:
                return getSpecialArrayStrategy(getArrayStrategy((ArrayType) componentType), componentType);
            case BOOLEAN:
            case INT:
            case SHORT:
            case BYTE:
            case CHAR:
            case LONG:
            case DOUBLE:
            case FLOAT:
                return getPrimitiveArrayStrategy((PrimitiveType) componentType);
            default:
                if (types.isSubtype(componentType, parcelable)) {
                    return getSpecialArrayStrategy(getParcelableStrategy(componentType), componentType);
                }

                final Strategy specialStrategy = getStrategy(componentType);

                if (specialStrategy != null) {
                    if (specialStrategy == SERIALIZABLE_STRATEGY) {
                        return getSerializableStrategy();
                    } else {
                        return getSpecialArrayStrategy(specialStrategy, componentType);
                    }
                }
        }

        final String arrayErrorMsg =
                "Unsupported array component type: " + componentType + ".\n" +
                "Must be one of:\n" +
                "\t• android.os.Parcelable, android.os.IInterface, java.io.Serializable, java.io.Externalizable\n" +
                "\t• One of types, natively supported by Parcel or one of primitive type wrappers\n" +
                "\t• Collection of supported type with public default constructor";

        throw new CodegenException(arrayErrorMsg);
    }

    private Strategy getSpecialArrayStrategy(Strategy delegate, TypeMirror componentType) {
        if (delegate == VOID_STRATEGY) {
            return VOID_STRATEGY;
        }

        // arrays don't support generics — the only thing, that matters, is a raw runtime type
        final TypeMirror resultType = types.erasure(componentType);

        final TypeMirror requestedType = delegate.requiredType;

        return Strategy.createNullSafe((block, name, actualType) -> {
            final String component = allocator.newName(Util.appendSuffix(name, "Component"));

            block.beginControlFlow("if ($L == null)", name);
            block.addStatement("$N.writeInt(-1)", parcelName);

            block.nextControlFlow("else");
            block.addStatement("$N.writeInt($L.length)", parcelName, name);

            block.beginControlFlow("for ($T $N : $L)", resultType, component, name);

            final boolean nullable = Util.isNullable(componentType, this.nullable);

            // now with casts!
            final CodeBlock elementBlock = emitFullCast(resultType, requestedType, Util.literal(component));

            if (nullable && delegate.needNullHandling) {
                getNullableStrategy(delegate)
                        .write(block, elementBlock, resultType);
            } else {
                delegate.write(block, elementBlock, resultType);
            }

            block.endControlFlow();

            block.endControlFlow();
        }, types.getArrayType(resultType));
    }

    private TypeMirror getReadableElementType(TypeMirror type) {
        final DeclaredType base = getCollectionInterface(type);

        final TypeInvocation<ExecutableElement, ExecutableType> specificIteratorMethod =
                collectionIterator.refine(types, base);

        final DeclaredType specificIteratorType = (DeclaredType) specificIteratorMethod.type.getReturnType();

        final TypeInvocation<ExecutableElement, ExecutableType> nextMethod =
                lookupMethod(theIterator, "next", "Object").refine(types, specificIteratorType);

        return nextMethod.type.getReturnType();
    }

    private Strategy getCollectionStrategy(TypeMirror type) throws CodegenException {
        // allow upper-bound wildcards to be used
        // not using captureAll() on purpose since any nested types with meaningful type args
        // (e.g. Collections) are going to be handled by recursive application of this method
        final TypeMirror noWildcards = types.capture(type);

        final TypeMirror elementType = getReadableElementType(noWildcards);

        final Strategy elementStrategy = getStrategy(elementType);

        if (elementStrategy == null) {
            return null;
        }

        // pretend, that this nonsense didn't happen
        if (elementStrategy == VOID_STRATEGY) {
            return null;
        }

        if (elementStrategy == SERIALIZABLE_STRATEGY && types.isAssignable(type, serializable)) {
            return getSerializableStrategy();
        }

        final TypeMirror concreteParent = findConcreteParent(type, theCollection);

        if (concreteParent == null) {
            if (!hasBound(type, listBound)  && !hasBound(type, setBound)) {
                return null;
            }
        } else {
            final TypeMirror captured = captureAll(type);

            if (!Util.isProperDeclared(captured)) {
                throw new IllegalStateException("Type " + captured + " was expected to be classy, but it is not");
            }

            final TypeElement te = (TypeElement) ((DeclaredType) captured).asElement();

            if (!hasPublicDefaultConstructor(te)) {
                return null;
            }
        }

        final TypeMirror requestedType = elementStrategy.requiredType;

        return Strategy.createNullSafe((block, name1, actualType) -> {
            final String element = allocator.newName(Util.appendSuffix(name1, "Element"));

            block.beginControlFlow("if ($L == null)", name1);
            block.addStatement("$N.writeInt(-1)", parcelName);

            block.nextControlFlow("else");
            block.addStatement("$N.writeInt($L.size())", parcelName, name1);

            final TypeMirror actualComponentType = getReadableElementType(actualType);

            if (types.isAssignable(actualComponentType, requestedType)) {
                block.beginControlFlow("for ($T $N : $L)", requestedType, element, name1);
            } else {
                block.beginControlFlow("for ($T $N : $T.<$T<$T>>unsafeCast($L))",
                        requestedType, element, ClassName.get(AidlUtil.class), ClassName.get(Iterable.class), requestedType, name1);
            }


            final boolean nullable = Util.isNullable(elementType, this.nullable);

            if (nullable && elementStrategy.needNullHandling) {
                getNullableStrategy(elementStrategy)
                        .write(block, element, requestedType);
            } else {
                elementStrategy.write(block, element, requestedType);
            }

            block.endControlFlow();

            block.endControlFlow();
        }, types.getDeclaredType(collectionElement, requestedType));
    }

    private Strategy getPrimitiveArrayStrategy(PrimitiveType component) {
        return Strategy.createNullSafe((block, name, unused) -> {
            switch (component.getKind()) {
                case BYTE:
                    block.addStatement("$L.writeByteArray($L)", parcelName, name);
                    break;
                case INT:
                    block.addStatement("$L.writeIntArray($L)", parcelName, name);
                    break;
                case BOOLEAN:
                    block.addStatement("$L.writeBooleanArray($L)", parcelName, name);
                    break;
                case CHAR:
                    block.addStatement("$L.writeCharArray($L)", parcelName, name);
                    break;
                case LONG:
                    block.addStatement("$L.writeLongArray($L)", parcelName, name);
                    break;
                case DOUBLE:
                    block.addStatement("$L.writeDoubleArray($L)", parcelName, name);
                    break;
                case FLOAT:
                    block.addStatement("$L.writeFloatArray($L)", parcelName, name);
                    break;
                default:
                    writeSerializable(block, name, types.getArrayType(component));
            }
        }, types.getArrayType(component));
    }

    private void writePrimitive(CodeBlock.Builder builder, Object name, PrimitiveType type) {
        switch (type.getKind()) {
            case LONG:
                builder.addStatement("$N.writeLong($L)", parcelName, name);
                break;
            case DOUBLE:
                builder.addStatement("$L.writeDouble($L)", parcelName, name);
                break;
            case FLOAT:
                builder.addStatement("$L.writeFloat($L)", parcelName, name);
                break;
            case BOOLEAN:
                builder.addStatement("$L.writeInt($L ? 1 : 0)", parcelName, name);
                break;
            default:
                builder.addStatement("$L.writeInt($L)", parcelName, name);
                break;
        }
    }

    private void writeExternalizable(CodeBlock.Builder block, Object name, TypeMirror ignored) {
        block.addStatement("$T.writeExternalizable($N, $L)", ClassName.get(AidlUtil.class), parcelName, name);
    }

    private void writeSerializable(CodeBlock.Builder block, Object name, TypeMirror ignored) {
        block.addStatement("$L.writeSerializable($L)", parcelName, name);
    }

    private static class Strategy implements WritingStrategy {
        private final WritingStrategy delegate;
        private final TypeMirror requiredType;
        private final boolean needNullHandling;

        private Strategy(WritingStrategy delegate, TypeMirror requiredType, boolean needNullHandling) {
            this.delegate = delegate;
            this.requiredType = requiredType;
            this.needNullHandling = needNullHandling;
        }

        public static Strategy create(WritingStrategy delegate, TypeMirror requiredType) {
            return new Strategy(delegate, requiredType, true);
        }

        public static Strategy createNullSafe(WritingStrategy delegate,  TypeMirror requiredType) {
            return new Strategy(delegate, requiredType, false);
        }

        @Override
        public void write(CodeBlock.Builder block, Object name, TypeMirror type) throws CodegenException {
            delegate.write(block, name, type);
        }
    }

    private interface WritingStrategy {
        void write(CodeBlock.Builder block, Object name, TypeMirror type) throws CodegenException;
    }
}
