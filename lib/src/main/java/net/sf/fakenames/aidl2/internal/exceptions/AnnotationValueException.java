package net.sf.fakenames.aidl2.internal.exceptions;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * An exception, describing validation error, related to annotation value.
 */
public final class AnnotationValueException extends ReadableException implements FaultyAnnotationValue {
    private final AnnotationMirror annotation;
    private final Element element;
    private final AnnotationValue value;

    public AnnotationValueException(String message, Element element, AnnotationMirror annotation, AnnotationValue value) {
        super(message);

        this.annotation = annotation;
        this.element = element;
        this.value = value;
    }

    @Override
    public Diagnostic.Kind getKind() {
        return Diagnostic.Kind.ERROR;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public AnnotationMirror getAnnotation() {
        return annotation;
    }

    @Override
    public AnnotationValue getValue() {
        return value;
    }
}
