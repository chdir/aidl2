package net.sf.aidl2.internal;

import net.sf.aidl2.Out;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

public final class AidlParamModel {
    @Nullable
    final CharSequence name;

    @NotNull
    final TypeMirror type;

    final int suppressed;

    final boolean nullable;

    final boolean inParameter;

    final boolean outParameter;

    final boolean varargParameter;

    private AidlParamModel(@Nullable CharSequence name,
                   @NotNull TypeMirror type,
                   int suppressed,
                   boolean nullable,
                   boolean inParameter,
                   boolean outParameter,
                   boolean varargParameter) {
        this.name = name;
        this.type = type;
        this.suppressed = suppressed;
        this.nullable = nullable;
        this.inParameter = inParameter;
        this.outParameter = outParameter;
        this.varargParameter = varargParameter;
    }

    public boolean isReturn() {
        return name == null;
    }

    public static AidlParamModel create(TypeInvocation<?, ?> paramInstance, boolean assumeNullable, int suppressedOnMethod) {
        final boolean isInParameter;
        final boolean hasInOut;
        final int suppressedOnParam;
        final boolean nullableParam;

        final CharSequence name;

        final ElementKind kind = paramInstance.element.getKind();

        switch (kind) {
            case PARAMETER:
                // method parameter
                isInParameter = true;

                hasInOut = paramInstance.element.getAnnotation(Out.class) != null;

                name = paramInstance.element.getSimpleName();

                suppressedOnParam = suppressedOnMethod | Util.getSuppressed(paramInstance.element);

                nullableParam = Util.isNullable(paramInstance.element, assumeNullable);
                break;
            case METHOD:
                // return value
                name = null;
                isInParameter = false;
                hasInOut = true;
                suppressedOnParam = suppressedOnMethod;
                nullableParam = assumeNullable;
                break;
            default:
                throw new IllegalArgumentException("Unsupported type of parameter!");
        }

        // why was this needed again?
        final boolean varArgParam = false;

        return new AidlParamModel(
                name,
                paramInstance.type,
                suppressedOnParam,
                nullableParam,
                isInParameter,
                hasInOut,
                varArgParam);
    }
}
