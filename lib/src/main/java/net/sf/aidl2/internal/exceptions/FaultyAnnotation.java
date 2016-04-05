package net.sf.aidl2.internal.exceptions;

import javax.lang.model.element.AnnotationMirror;

public interface FaultyAnnotation extends FaultyElement {
    AnnotationMirror getAnnotation();
}
