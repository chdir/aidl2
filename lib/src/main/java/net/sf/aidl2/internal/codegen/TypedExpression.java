package net.sf.aidl2.internal.codegen;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.type.TypeMirror;

public final class TypedExpression {
    public final CodeBlock code;
    public final TypeMirror type;

    public TypedExpression(CodeBlock code, TypeMirror type) {
        this.code = code;
        this.type = type;
    }
}
