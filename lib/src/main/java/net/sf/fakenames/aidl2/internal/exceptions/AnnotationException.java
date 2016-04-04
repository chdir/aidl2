package net.sf.fakenames.aidl2.internal.exceptions;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * An exception, describing validation error, related to placement of annotation.
 */
public final class AnnotationException extends ReadableException implements FaultyAnnotation {
    private final AnnotationMirror annotation;
    private final Element element;

    public AnnotationException(String message, Element element, AnnotationMirror annotation) {
        super(message);

        this.element = element;
        this.annotation = annotation;
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
}
