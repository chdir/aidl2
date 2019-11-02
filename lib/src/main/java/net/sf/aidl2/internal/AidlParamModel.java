package net.sf.aidl2.internal;

import net.sf.aidl2.As;
import net.sf.aidl2.DataKind;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

final class AidlParamModel {
    @Nullable
    final CharSequence name;

    @NotNull
    final TypeMirror type;

    @NotNull
    final DataKind strategy;

    final int suppressed;

    final boolean nullable;

    final boolean inParameter;

    final boolean outParameter;

    final boolean varargParameter;

    private AidlParamModel(@Nullable CharSequence name,
                   @NotNull TypeMirror type,
                   @NotNull DataKind strategy,
                   int suppressed,
                   boolean nullable,
                   boolean inParameter,
                   boolean outParameter,
                   boolean varargParameter) {
        this.name = name;
        this.type = type;
        this.strategy = strategy;
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

        final AnnotationMirror argAnnotation = Util.getAnnotation(paramInstance.element, As.class);

        final DataKind strategy = getStrategy(argAnnotation);

        final ElementKind kind = paramInstance.element.getKind();

        switch (kind) {
            case PARAMETER:
                // method parameter
                isInParameter = true;

                hasInOut = false;

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
                strategy,
                suppressedOnParam,
                nullableParam,
                isInParameter,
                hasInOut,
                varArgParam);
    }

    private static DataKind getStrategy(AnnotationMirror arg) {
        if (arg == null) {
            return DataKind.AUTO;
        }

        Map<? extends ExecutableElement, ? extends AnnotationValue> v = arg.getElementValues();

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : v.entrySet()) {
            ExecutableElement ex = e.getKey();

            String name = ex.getSimpleName().toString();

            switch (name) {
                case "value":
                    return e.getValue().accept(new ArgKindVisitor(), null);
            }
        }

        return DataKind.AUTO;
    }
}
