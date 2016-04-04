package net.sf.fakenames.aidl2.internal;

import net.sf.fakenames.aidl2.AIDL;
import net.sf.fakenames.aidl2.External;
import net.sf.fakenames.aidl2.OneWay;
import net.sf.fakenames.aidl2.Out;
import net.sf.fakenames.aidl2.internal.codegen.MethodInstantiation;
import net.sf.fakenames.aidl2.internal.exceptions.AnnotationException;
import net.sf.fakenames.aidl2.internal.exceptions.ElementException;
import net.sf.fakenames.aidl2.internal.util.Util;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

final class AidlMethodValidator extends AptHelper {
    private final TypeMirror stringClass;
    private final TypeMirror enumType;
    private final TypeMirror remoteException;

    private final DeclaredType interfaceType;
    private final AIDL annotation;

    public AidlMethodValidator(AidlProcessor.Environment environment, DeclaredType interfaceType) {
        super(environment);

        this.interfaceType = interfaceType;

        this.annotation = interfaceType.getAnnotation(AIDL.class);
        
        stringClass = lookup(String.class);
        enumType = lookup(Enum.class);

        remoteException = lookup("android.os.RemoteException");
    }

    void validate(MethodInstantiation method) throws AnnotationException, ElementException {
        final AnnotationMirror asyncAnnotation = Util.getAnnotation(method.element, OneWay.class);

        if (asyncAnnotation != null) {
            final TypeMirror returnType = method.type.getReturnType();

            if (!isVoid(returnType)) {
                throw new AnnotationException("@OneWay-annotated methods must be void or Void", method.element, asyncAnnotation);
            }
        }

        final List<? extends TypeMirror> thrown = method.type.getThrownTypes();

        if (thrown.size() != 1 || !types.isSameType(remoteException, thrown.get(0))) {
            throw new ElementException("@AIDL methods must declare single thrown exception — android.os.RemoteException", method.element);
        }

        final List<? extends VariableElement> params = method.element.getParameters();
        final List<? extends TypeMirror> paramTypes = method.type.getParameterTypes();

        for (int i = 0; i < params.size(); i++) {
            final VariableElement param = params.get(i);
            final TypeMirror paramType = paramTypes.get(i);

            final Iterable<? extends AnnotationMirror> mirrors = param.getAnnotationMirrors();

            final AnnotationMirror externalAnnotation = Util.getAnnotation(mirrors, External.class);

            if (externalAnnotation != null) {
                if (paramType.getKind().isPrimitive()) {
                    throw new AnnotationException("@External-annotated parameter must be of reference type", param, externalAnnotation);
                }

                if (!annotation.insecure()) {
                    throw new AnnotationException("@External-annotated parameter found, but 'insecure' parameter in @AIDL is not set to true", param, externalAnnotation);
                }
            }

            final AnnotationMirror outAnnotation = Util.getAnnotation(mirrors, Out.class);

            if (outAnnotation != null) {
                if (asyncAnnotation != null) {
                    throw new AnnotationException("@OneWay methods can not have @Out parameters", param, outAnnotation);
                }

                if (paramType.getKind().isPrimitive()) {
                    throw new AnnotationException("@Out-annotated parameter must be of reference type, not " + paramType, param, outAnnotation);
                }

                if (isKnownImmutable(paramType)) {
                    throw new AnnotationException("@Out-annotated parameter is meant to be mutable, not " + paramType, param, outAnnotation);
                }
            }
        }
    }

    private boolean isKnownImmutable(TypeMirror paramType) {
        return types.unboxedType(paramType) != null
                || types.isSubtype(paramType, stringClass)
                || types.isSubtype(paramType, enumType);
    }
}
