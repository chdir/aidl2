package net.sf.fakenames.aidl2.internal.codegen;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

final class ArrayInitBlock {
    private final TypeMirror componentType;
    private final Object lengthLiteral;

    public ArrayInitBlock(TypeMirror component, Object lengthLiteral) {
        this.componentType = component;
        this.lengthLiteral = lengthLiteral;
    }

    public CodeBlock toBlock() {
        switch (componentType.getKind()) {
            case ARRAY:
                final TypeMirror subComponentType =
                        ((ArrayType) componentType).getComponentType();

                final ArrayInitBlock nestedBlock =
                        new ArrayInitBlock(subComponentType, lengthLiteral);

                return CodeBlock.builder()
                        .add(nestedBlock.toBlock())
                        .add("[]")
                        .build();
            default:
                return CodeBlock.builder()
                        .add("$T[$L]", componentType, lengthLiteral)
                        .build();
        }
    }
}
