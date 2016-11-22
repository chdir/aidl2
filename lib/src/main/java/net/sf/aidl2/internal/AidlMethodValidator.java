package net.sf.aidl2.internal;

import android.os.IBinder;
import net.sf.aidl2.AIDL;
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
import javax.lang.model.element.ExecutableElement;
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

        final List<? extends TypeMirror> thrown = method.type.getThrownTypes();

        for (TypeMirror throwable : thrown) {
            if (isChecked(throwable)) {
                if (types.isSameType(remoteException, throwable)) {
                    return;
                }

                throw new ElementException("@AIDL method declares unsupported Exception type: " + throwable
                         + ". Only android.os.RemoteException and non-checked Throwable subtypes are allowed.", method.element);
            }
        }

        throw new ElementException("@AIDL methods must declare android.os.RemoteException", method.element);
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
}
