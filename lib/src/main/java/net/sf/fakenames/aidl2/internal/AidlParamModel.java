package net.sf.fakenames.aidl2.internal;

import net.sf.fakenames.aidl2.Out;
import net.sf.fakenames.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public final class AidlParamModel {
    @Nullable
    final CharSequence name;

    @NotNull
    final TypeMirror type;

    final boolean unchecked;

    final boolean nullable;

    final boolean inParameter;

    final boolean outParameter;

    final boolean varargParameter;

    AidlParamModel(@Nullable CharSequence name,
                   @NotNull TypeMirror type,
                   boolean unchecked,
                   boolean nullable,
                   boolean inParameter,
                   boolean outParameter,
                   boolean varargParameter) {
        this.name = name;
        this.type = type;
        this.unchecked = unchecked;
        this.nullable = nullable;
        this.inParameter = inParameter;
        this.outParameter = outParameter;
        this.varargParameter = varargParameter;
    }

    public boolean isReturn() {
        return name == null;
    }

    public static AidlParamModel create(TypeMirror paramType, VariableElement paramEl, boolean assumeNullable, boolean assumeUnchecked) {
        final SuppressWarnings paramSw = paramEl.getAnnotation(SuppressWarnings.class);

        final boolean suppressUncheckedOnParam = assumeUnchecked || Util.isSuppressed("unchecked", paramSw);

        final boolean nullable = Util.isNullable(paramEl, assumeNullable);

        final boolean varArgParam = false;

        final boolean hasInOut = paramEl.getAnnotation(Out.class) != null;

        return new AidlParamModel(
                paramEl.getSimpleName(),
                paramType,
                suppressUncheckedOnParam,
                nullable,
                true,
                hasInOut,
                varArgParam);
    }
}
