package net.sf.aidl2.internal;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.OneWay;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.exceptions.AnnotationException;
import net.sf.aidl2.internal.exceptions.ElementException;
import net.sf.aidl2.internal.util.Util;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

final class AidlMethodValidator extends AptHelper {
    private final TypeMirror stringClass;
    private final TypeMirror enumType;
    private final TypeMirror remoteException;

    private final AIDL annotation;

    public AidlMethodValidator(AidlProcessor.Environment environment, DeclaredType interfaceType) {
        super(environment);

        this.annotation = interfaceType.getAnnotation(AIDL.class);
        
        stringClass = lookup(String.class);
        enumType = lookup(Enum.class);

        remoteException = lookup("android.os.RemoteException");
    }

    void validate(TypeInvocation<ExecutableElement, ExecutableType> method) throws AnnotationException, ElementException {
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
    }

    private boolean isKnownImmutable(TypeMirror paramType) {
        return types.unboxedType(paramType) != null
                || types.isSubtype(paramType, stringClass)
                || types.isSubtype(paramType, enumType);
    }
}
