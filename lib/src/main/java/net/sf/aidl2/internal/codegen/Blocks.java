package net.sf.aidl2.internal.codegen;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.type.TypeMirror;

public final class Blocks {
    public static CodeBlock arrayInit(TypeMirror arrayComponent, Object countLiteral) {
        return new ArrayInitBlock(arrayComponent, countLiteral).toBlock();
    }

    private Blocks() {}
}
