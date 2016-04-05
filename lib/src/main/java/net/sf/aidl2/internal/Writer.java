package net.sf.aidl2.internal;

import android.os.Parcel;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.NameAllocator;

import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

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

        if (state.returnValue) {
            flags = Util.literal("$T.PARCELABLE_WRITE_RETURN_VALUE", parcelable);
        } else {
            flags = 0;
        }
    }

    public TypeMirror write(CodeBlock.Builder paramWriting, TypeMirror type) throws CodegenException {
        Strategy strategy = getStrategy(type);

        if (strategy != null) {
            if (nullable && strategy.needNullHandling) {
                getNullableStrategy(strategy)
                        .write(paramWriting, name);
            } else {
                strategy.write(paramWriting, name);
            }

            return strategy.requiredType;
        }

        final String errorMsg =
                "Unsupported type: " + type + ".\n" +
                "Must be one of:\n" +
                "\t• android.os.Parcelable\n" +
                "\t• java.io.Serializable\n" +
                "\t• java.io.Externalizable\n" +
                "\t• One of types, natively supported by Parcel\n" +
                "\t• One of primitive type wrappers.";

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
                return Strategy.createNullSafe((b, n) -> writePrimitive(b, n, (PrimitiveType) type), type);
            case ARRAY:
                return getArrayStrategy((ArrayType) type);
            case DECLARED:
                final CharSequence name = Util.getQualifiedName((DeclaredType) type);
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
                                return Strategy.create((b, n) -> writePrimitive(b, n, primitiveVariety), type);
                            }
                            break;
                        case "java.lang.Void":
                            return Strategy.createNullSafe(($, $$) -> {
                            }, theObject);
                    }
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

                // Or at least Externalizable strategy
                if (types.isAssignable(type, externalizable)) {
                    return getExternalizableStrategy(type);
                }

                if (types.isAssignable(type, serializable)) {
                    return getSerializableStrategy();
                }
        }

        return null;
    }

    private Strategy getSerializableStrategy() {
        return Strategy.createNullSafe(this::writeSerializable, serializable);
    }

    private Strategy getExternalizableStrategy(TypeMirror type) {
        return Strategy.create((block, name) -> {
            final String oos = allocator.newName("objectOutputStream");
            final String baos = allocator.newName("arrayOutputStream");
            final String err = allocator.newName("e");

            block.addStatement("$T $N = null", ObjectOutputStream.class, oos);

            block.beginControlFlow("try");

            block.addStatement("$T $N = new $T()",
                    ByteArrayOutputStream.class, baos, ByteArrayOutputStream.class);
            block.addStatement("$N = new $T($N)", oos, ObjectOutputStream.class, baos);

            block.addStatement("$L.writeExternal($N)", name, oos);
            block.addStatement("$N.writeByteArray($N.toByteArray())", parcelName, baos);

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

        return Strategy.create((block, name) -> block.addStatement("$L.writeToParcel($L, $L)", name, parcelName, flags), parcelable);
    }

    private Strategy getAbstractParcelableStrategy() {
        return Strategy.createNullSafe((b, name) -> b.addStatement("$L.writeParcelable($L, $L)", parcelName, name, flags), parcelable);
    }

    private Strategy getBuiltinStrategy(TypeMirror type) throws CodegenException {
        if (types.isAssignable(type, sizeF)) {
            return Strategy.create((block, name) -> block.addStatement("$L.writeSizeF($L)", parcelName, name), sizeF);
        } else if (types.isAssignable(type, sizeType)) {
            return Strategy.create((block, name) -> block.addStatement("$L.writeSize($L)", parcelName, name), sizeType);
        }
        // supported via native methods, always nullable
        else if (types.isAssignable(type, string)) {
            return Strategy.createNullSafe((block, name) -> block.addStatement("$L.writeString($L)", parcelName, name), string);
        } else if (types.isAssignable(type, iBinder)) {
            return Strategy.createNullSafe((block, name) -> block.addStatement("$L.writeStrongBinder($L)", parcelName, name), iBinder);
        }
        // supported via non-standard method, always nullable
        else if (types.isAssignable(type, charSequence)) {
            return Strategy.createNullSafe((block, name) -> block.addStatement("$T.writeToParcel($L, $L, $L)", textUtils, name, parcelName, flags), charSequence);
        }
        // containers, so naturally nullable
        else if (types.isAssignable(type, sparseBoolArray)) {
            return Strategy.createNullSafe((block, name) -> block.addStatement("$L.writeSparseBooleanArray($L)", parcelName, name), sparseBoolArray);
        } else if (types.isAssignable(type, bundle)) {
            return Strategy.createNullSafe((block, name) -> block.addStatement("$L.writeBundle($L)", parcelName, name), bundle);
        } else if (types.isAssignable(type, persistable)) {
            return Strategy.createNullSafe((block, name) -> block.addStatement("$L.writePersistableBundle($L)", parcelName, name), persistable);
        } else {
            if (isEffectivelyObject(type)) {
                if (allowUnchecked) {
                    return Strategy.createNullSafe((block, name) -> block.addStatement("$N.writeValue($L)", parcelName, name), theObject);
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
        return Strategy.createNullSafe((block, name) -> {
            block.beginControlFlow("if ($L == null)", name);
            block.addStatement("$N.writeByte((byte) -1)", parcelName);
            block.nextControlFlow("else");
            block.addStatement("$N.writeByte((byte) 0)", parcelName);
            strategy.write(block, name);
            block.endControlFlow();
        }, strategy.requiredType);
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
                    return getSpecialArrayStrategy(getParcelableStrategy(component), component);
                }

                final Strategy specialStrategy = getStrategy(component);

                if (specialStrategy != null) {
                    return getSpecialArrayStrategy(specialStrategy, component);
                }

                if (types.isAssignable(component, serializable)) {
                    return getSerializableStrategy();
                }
        }

        final String arrayErrorMsg =
                "Unsupported array component type: " + component + ".\n" +
                "Must be one of:\n" +
                "\t• android.os.Parcelable\n" +
                "\t• java.io.Serializable\n" +
                "\t• java.io.Externalizable\n" +
                "\t• One of types, natively supported by Parcel\n" +
                "\t• One of primitive type wrappers.";

        throw new CodegenException(arrayErrorMsg);
    }

    private Strategy getSpecialArrayStrategy(Strategy delegate, TypeMirror component) {
        // arrays don't support generics — the only thing, that matters, is a raw runtime type
        final TypeMirror resultType = types.erasure(component);

        final TypeMirror requestedType = delegate.requiredType;

        return Strategy.createNullSafe((block, name) -> {
            final String element = allocator.newName(name + "Element");

            block.beginControlFlow("for ($T $N : $L)", resultType, element, name);

            final boolean nullable = Util.isNullable(component, this.nullable);

            // now with casts!
            final CodeBlock elementBlock = emitFullCast(resultType, requestedType, Util.literal(element));

            if (nullable && delegate.needNullHandling) {
                getNullableStrategy(delegate)
                        .write(block, elementBlock);
            } else {
                delegate.write(block, elementBlock);
            }

            block.endControlFlow();
        }, types.getArrayType(resultType));
    }

    private Strategy getPrimitiveArrayStrategy(PrimitiveType component) {
        return Strategy.createNullSafe((block, name) -> {
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
                    writeSerializable(block, name);
            }
        }, types.getArrayType(component));
    }

    private void writePrimitive(CodeBlock.Builder builder, Object name, PrimitiveType type) {
        switch (type.getKind()) {
            case LONG:
                builder.addStatement("$N.writeLong($L)", parcelName, name);
                break;
            case DOUBLE:
                builder.addStatement("$L.writeDouble($:)", parcelName, name);
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

    private void writeSerializable(CodeBlock.Builder block, Object name) {
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
        public void write(CodeBlock.Builder block, Object name) throws CodegenException {
            delegate.write(block, name);
        }
    }

    private interface WritingStrategy {
        void write(CodeBlock.Builder block, Object name) throws CodegenException;
    }
}