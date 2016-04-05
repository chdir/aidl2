package net.sf.aidl2.internal.exceptions;

import javax.lang.model.element.AnnotationValue;

public interface FaultyAnnotationValue extends FaultyAnnotation {
    AnnotationValue getValue();
}
