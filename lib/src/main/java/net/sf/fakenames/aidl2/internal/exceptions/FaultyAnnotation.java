package net.sf.fakenames.aidl2.internal.exceptions;

import javax.lang.model.element.AnnotationMirror;

public interface FaultyAnnotation extends FaultyElement {
    AnnotationMirror getAnnotation();
}
