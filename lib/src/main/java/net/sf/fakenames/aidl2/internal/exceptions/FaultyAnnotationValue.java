package net.sf.fakenames.aidl2.internal.exceptions;

import javax.lang.model.element.AnnotationValue;

public interface FaultyAnnotationValue extends FaultyAnnotation {
    AnnotationValue getValue();
}
