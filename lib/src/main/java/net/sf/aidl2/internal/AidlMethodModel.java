package net.sf.aidl2.internal;

import net.sf.aidl2.Call;
import net.sf.aidl2.OneWay;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

public final class AidlMethodModel {
    @NotNull
    final AidlParamModel[] parameters;

    final int warningsSuppressedOnMethod;

    final boolean oneWay;

    final int idxInFile;

    int transactionId;

    @NotNull
    final TypeInvocation<ExecutableElement, ExecutableType> element;

    @NotNull
    final String[] suppressed;

    private AidlMethodModel(
            @NotNull TypeInvocation<ExecutableElement, ExecutableType> element,
            @NotNull AidlParamModel[] parameters,
            int idxInFile,
            int transactionId,
            final int unchecked,
            final boolean oneWay,
            @NotNull final String... suppressed) {
        this.element = element;
        this.parameters = parameters;
        this.idxInFile = idxInFile;
        this.transactionId = transactionId;
        this.oneWay = oneWay;
        this.suppressed = suppressed;
        this.warningsSuppressedOnMethod = unchecked;
    }

    public static AidlMethodModel create(
            TypeInvocation<ExecutableElement, ExecutableType> methodEl,
            int idxInFile,
            boolean nullableOnType,
            int suppressedOnType) {
        int transactionId = -1;

        if (idxInFile != -1) {
            final Call methodMetadata = methodEl.element.getAnnotation(Call.class);

            if (methodMetadata != null) {
                transactionId = methodMetadata.value();
            }
        }

        final boolean isOneWay = methodEl.element.getAnnotation(OneWay.class) != null;

        final SuppressWarnings methodSw = methodEl.element.getAnnotation(SuppressWarnings.class);

        final int suppressedOnMethod = suppressedOnType | Util.getSuppressed(methodSw);

        final String[] methodTransplanted = methodSw != null ? methodSw.value() : new String[0];

        final List<? extends VariableElement> params = methodEl.element.getParameters();

        final TypeMirror returnType = methodEl.type.getReturnType();

        final AidlParamModel[] parameterModels = new AidlParamModel[params.size() + 1];

        final boolean nullable = Util.isNullable(methodEl.element, nullableOnType);

        parameterModels[params.size()] = AidlParamModel.create(new TypeInvocation<>(methodEl.element, returnType), nullable, suppressedOnMethod);

        final List<? extends TypeMirror> paramTypes = methodEl.type.getParameterTypes();

        for (int i = 0; i < params.size(); i++) {
            final VariableElement paramEl = params.get(i);
            final TypeMirror paramType = paramTypes.get(i);

            parameterModels[i] = AidlParamModel.create(new TypeInvocation<>(paramEl, paramType), nullable, suppressedOnMethod);
        }

        return new AidlMethodModel(
                new TypeInvocation<>(methodEl.element, methodEl.type),
                parameterModels,
                idxInFile,
                transactionId,
                suppressedOnMethod,
                isOneWay,
                methodTransplanted);
    }

    boolean isDeclaredInPrimaryFile() {
        return idxInFile != -1;
    }
}