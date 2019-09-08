package net.sf.aidl2.internal;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.internal.exceptions.AnnotationException;
import net.sf.aidl2.internal.exceptions.AnnotationValueException;
import net.sf.aidl2.internal.util.SuperficialValidation;
import net.sf.aidl2.internal.util.Util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

final class AidlValidator extends AptHelper {
    private final DeclaredType iInterface;

    public AidlValidator(AidlProcessor.Environment environment) {
        super(environment);

        iInterface = lookup("android.os.IInterface");
    }

    Element validate(TypeElement element) throws AnnotationException, AnnotationValueException {
        final Element errorElement = new SuperficialValidation(getBaseEnvironment()).validateElement(element);

        if (errorElement != null) {
            return errorElement;
        }

        final AnnotationMirror aidlAnnotation = Util.getAnnotation(element, AIDL.class);

        if (aidlAnnotation == null) {
            throw new IllegalStateException("Failed to get annotation mirror from @AIDL-annotated element");
        }

        if (element.getKind() != ElementKind.INTERFACE) {
            throw new AnnotationException("@AIDL is allowed on interfaces only!", element, aidlAnnotation);
        }

        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            throw new AnnotationException("@AIDL-annotated interfaces must be public", element, aidlAnnotation);
        }

        if (!types.isSubtype(element.asType(), iInterface)) {
            throw new AnnotationException("@AIDL-annotated interfaces must extend android.os.IInterface", element, aidlAnnotation);
        }

        return null;
    }
}