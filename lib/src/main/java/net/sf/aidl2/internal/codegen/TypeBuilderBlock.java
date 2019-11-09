package net.sf.aidl2.internal.codegen;

import com.squareup.javapoet.CodeBlock;

import net.sf.aidl2.AidlUtil;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

final class TypeBuilderBlock {
    private static final CodeBlock objectBlock = CodeBlock.builder()
            .add("$T.class", Object.class)
            .build();

    private final Types types;

    private final TypeMirror type;

    public TypeBuilderBlock(Types types, TypeMirror type) {
        this.types = types;
        this.type = type;
    }

    public CodeBlock toBlock() {
        switch (type.getKind()) {
            case DECLARED:
                final List<? extends TypeMirror> typeArgs = ((DeclaredType) type).getTypeArguments();

                if (typeArgs.isEmpty()) {
                    return CodeBlock.builder()
                            .add("$T.class", type)
                            .build();
                } else {
                    CodeBlock.Builder builder = CodeBlock.builder();

                    DeclaredType noArgType = types.getDeclaredType((TypeElement) types.asElement(type));

                    builder.add("$T.of($T.class", AidlUtil.class, noArgType);

                    for (int i = 0; i < typeArgs.size(); ++i) {
                        builder.add(", ");

                        builder.add(new TypeBuilderBlock(types, typeArgs.get(i)).toBlock());
                    }

                    builder.add(")");

                    return builder.build();
                }
            case ARRAY:
                StringBuilder builder = new StringBuilder();

                TypeMirror mirror = type;
                do {
                    mirror = ((ArrayType) mirror).getComponentType();

                    builder.append("[]");
                }
                while (mirror.getKind() != TypeKind.ARRAY);

                return CodeBlock.builder()
                        .add("$T$L.class", mirror, builder)
                        .build();

            default:
                // TODO: try to erase it?
                return objectBlock;
        }
    }
}
