package net.sf.fakenames.aidl2.internal;

import net.sf.fakenames.aidl2.internal.codegen.MethodInstantiation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public final class AidlMethodModel {
    @NotNull
    final AidlParamModel[] parameters;

    final boolean unchecked;

    final boolean oneWay;

    @NotNull
    final MethodInstantiation element;

    @NotNull
    final String[] suppressed;

    private AidlMethodModel(
            @NotNull MethodInstantiation element,
            @NotNull AidlParamModel[] parameters,
            final boolean unchecked,
            final boolean oneWay,
            @NotNull final String... suppressed) {
        this.element = element;
        this.parameters = parameters;
        this.oneWay = oneWay;
        this.suppressed = suppressed;
        this.unchecked = unchecked;
    }

    public static AidlMethodModel create(MethodInstantiation methodEl, boolean nullableOnType, boolean uncheckedOnType) {
        final boolean isOneWay = methodEl.element.getAnnotation(net.sf.fakenames.aidl2.OneWay.class) != null;

        final SuppressWarnings methodSw = methodEl.element.getAnnotation(SuppressWarnings.class);

        final boolean suppressUncheckedOnMethod = uncheckedOnType || methodSw != null && net.sf.fakenames.aidl2.internal.util.Util.isSuppressed("unchecked", methodSw);

        final String[] methodTransplanted = methodSw != null ? methodSw.value() : new String[0];

        final List<? extends VariableElement> params = methodEl.element.getParameters();

        final TypeMirror returnType = methodEl.type.getReturnType();

        final AidlParamModel[] parameterModels = new AidlParamModel[params.size() + 1];

        final boolean nullable = net.sf.fakenames.aidl2.internal.util.Util.isNullable(methodEl.element, nullableOnType);

        parameterModels[params.size()] = new AidlParamModel(null,
                returnType, suppressUncheckedOnMethod, nullable, false, true, false);

        final List<? extends TypeMirror> paramTypes = methodEl.type.getParameterTypes();

        for (int i = 0; i < params.size(); i++) {
            final VariableElement paramEl = params.get(i);
            final TypeMirror paramType = paramTypes.get(i);

            parameterModels[i] = AidlParamModel.create(paramType, paramEl, nullable, suppressUncheckedOnMethod);
        }

        return new AidlMethodModel(
                new MethodInstantiation(methodEl.element, methodEl.type),
                parameterModels,
                suppressUncheckedOnMethod,
                isOneWay,
                methodTransplanted);
    }
}