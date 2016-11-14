package net.sf.aidl2.internal.codegen;

import com.squareup.javapoet.CodeBlock;
import net.sf.aidl2.internal.util.Util;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

final class ArrayInitBlock {
    private final TypeMirror componentType;
    private final Object lengthLiteral;
    private final boolean erased;

    public ArrayInitBlock(TypeMirror component, boolean erased, Object lengthLiteral) {
        this.componentType = component;
        this.lengthLiteral = lengthLiteral;
        this.erased = erased;
    }

    public CodeBlock toBlock() {
        switch (componentType.getKind()) {
            case ARRAY:
                final TypeMirror subComponentType =
                        ((ArrayType) componentType).getComponentType();

                final ArrayInitBlock nestedBlock =
                        new ArrayInitBlock(subComponentType, erased, lengthLiteral);

                return CodeBlock.builder()
                        .add(nestedBlock.toBlock())
                        .add("[]")
                        .build();
            default:
                return CodeBlock.builder()
                        .add(erased ? "$T<?>[$L]" : "$T[$L]", componentType, lengthLiteral)
                        .build();
        }
    }
}
