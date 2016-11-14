package net.sf.aidl2.internal.codegen;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public final class Blocks {
    public static CodeBlock arrayInit(Types types, TypeMirror arrayComponent, Object countLiteral) {
        final TypeMirror erased = types.erasure(arrayComponent);

        return new ArrayInitBlock(erased, !types.isSameType(erased, arrayComponent), countLiteral).toBlock();
    }

    private Blocks() {}
}
