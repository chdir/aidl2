package net.sf.aidl2.internal;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.As;
import net.sf.aidl2.DataKind;
import net.sf.aidl2.Call;
import net.sf.aidl2.OneWay;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.exceptions.AnnotationException;
import net.sf.aidl2.internal.exceptions.AnnotationValueException;
import net.sf.aidl2.internal.exceptions.ElementException;
import net.sf.aidl2.internal.util.Util;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.tools.Diagnostic;

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

    void validate(TypeInvocation<ExecutableElement, ExecutableType> method) throws AnnotationException, ElementException, AnnotationValueException {
        final AnnotationMirror asyncAnnotation = Util.getAnnotation(method.element, OneWay.class);

        if (asyncAnnotation != null) {
            final TypeMirror returnType = method.type.getReturnType();

            if (!isVoid(returnType)) {
                throw new AnnotationException("@OneWay-annotated methods must be void or Void", method.element, asyncAnnotation);
            }
        }

        final AnnotationMirror callAnnotation = Util.getAnnotation(method.element, Call.class);

        if (callAnnotation != null) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = callAnnotation.getElementValues();

            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
                String name = e.getKey().getSimpleName().toString();

                switch (name) {
                    case "value":
                        final Integer value = e.getValue().accept(getTransIdVisitor(), null);

                        if (!isTransactionValid(value)) {
                            if ((Util.getSuppressed(method.element) & Util.SUPPRESS_AIDL) == 0) {
                                String msg = "Invalid transaction id: " + value + ".\n" +
                                        "Must be between FIRST_CALL_TRANSACTION and LAST_CALL_TRANSACTION";

                                throw new AnnotationValueException(msg, method.element, callAnnotation, e.getValue());
                            } else {
                                getMessager().printMessage(Diagnostic.Kind.NOTE, "Transaction id " + value +
                                        " collides with range of system-reserved values", method.element, callAnnotation, e.getValue());
                            }
                        }
                }
            }
        }

        final AnnotationMirror returnValueAnnotation = Util.getAnnotation(method.element, As.class);

        if (returnValueAnnotation != null) {
            validateArgAnnotation(method.element, method.type.getReturnType(), returnValueAnnotation);
        }

        List<? extends VariableElement> args = method.element.getParameters();

        for (VariableElement arg : args) {
            final AnnotationMirror argAnnotation = Util.getAnnotation(arg, As.class);

            if (argAnnotation != null) {
                validateArgAnnotation(arg, arg.asType(), argAnnotation);
            }
        }

        final List<? extends TypeMirror> thrown = method.type.getThrownTypes();

        boolean throwsRemoteException = false;

        for (TypeMirror throwable : thrown) {
            if (isChecked(throwable)) {
                if (types.isSameType(remoteException, throwable)) {
                    throwsRemoteException = true;
                    continue;
                }

                throw new ElementException("@AIDL method declares unsupported Exception type: " + throwable
                        + ". Only android.os.RemoteException and non-checked Throwable subtypes are allowed.", method.element);
            }
        }

        if (!throwsRemoteException) {
            throw new ElementException("@AIDL methods must declare android.os.RemoteException", method.element);
        }
    }

    private void validateArgAnnotation(Element element, TypeMirror type, AnnotationMirror argAnnotation) throws AnnotationValueException {
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = argAnnotation.getElementValues();

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
            String name = e.getKey().getSimpleName().toString();

            switch (name) {
                case "value":
                    final DataKind value = e.getValue().accept(getArgKindVisitor(), null);

                    if (value == null) {
                        String msg = "Unknown argument type: " + e.getValue() + ".\n" +
                                "Make sure, that you use matching versions of processor and dependencies";

                        throw new AnnotationValueException(msg, element, argAnnotation, e.getValue());
                    }

                    if (!isCompatible(value, type)) {
                        String msg = "Type " +  type + " is incompatible with DataKind." + value + ".\n";

                        throw new AnnotationValueException(msg, element, argAnnotation, e.getValue());
                    }
                    break;
                case "converter":
                    validateConverter(element, argAnnotation, e.getValue());
                    break;
            }
        }
    }

    private void validateConverter(Element variable, AnnotationMirror annotation, AnnotationValue value) throws AnnotationValueException {
        final TypeMirror mirror = (TypeMirror) value.getValue();

        Element element = types.asElement(mirror);
        if (element == null || element.getKind() != ElementKind.CLASS) {
            String msg = "Type " + mirror + " can not be used in this annotation: must be a class.\n";

            throw new AnnotationValueException(msg, variable, annotation, value);
        }

        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
            String msg = "Type " + mirror + " can not be used as converter: it is abstract.\n";

            throw new AnnotationValueException(msg, variable, annotation, value);
        }

        TypeElement converterClass = (TypeElement) element;

        boolean hasDefaultConstructor = false;

        for (Element enclosed : converterClass.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.CONSTRUCTOR) {
                continue;
            }

            ExecutableElement constructorElement = (ExecutableElement) enclosed;
            if (constructorElement.getParameters().isEmpty()
                    && !constructorElement.getModifiers().contains(Modifier.PRIVATE)) {
                hasDefaultConstructor = true;
                break;
            }
        }

        if (!hasDefaultConstructor) {
            String msg = "Type " + mirror + " must have a no-arg constructor to be used as converter.\n";

            throw new AnnotationValueException(msg, variable, annotation, value);
        }
    }

    private boolean isCompatible(DataKind strategy, TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            switch (strategy) {
                default:
                    return false;
                case SERIALIZABLE:
                case AUTO:
                    return true;
            }
        }

        if (strategy == DataKind.MAP) {
            return types.isAssignable(type, theMap);
        }

        return true;
    }

    private static IdVisitor ID_VISITOR;

    private IdVisitor getTransIdVisitor() {
        if (ID_VISITOR == null) {
            ID_VISITOR = new IdVisitor();
        }

        return ID_VISITOR;
    }

    private static final class IdVisitor extends SimpleAnnotationValueVisitor6<Integer, Void> {
        @Override
        public Integer visitInt(int i, Void aVoid) {
            return i;
        }
    };

    private int minUser = 0x00000001;
    private int maxUser = 0x00ffffff;

    private boolean isTransactionValid(Integer transactionId) {
        return transactionId == null || !(transactionId < minUser || transactionId > maxUser);
    }

    private static ArgKindVisitor KIND_VISITOR;

    private ArgKindVisitor getArgKindVisitor() {
        if (KIND_VISITOR == null) {
            KIND_VISITOR = new ArgKindVisitor();
        }

        return KIND_VISITOR;
    }
}
