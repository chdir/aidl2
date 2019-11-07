package net.sf.aidl2.internal;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

final class ArgConverterVisitor extends SimpleAnnotationValueVisitor6<TypeMirror, Void> {
    @Override
    public TypeMirror visitType(TypeMirror typeMirror, Void aVoid) {
        return typeMirror;
    }

    @Override
    public TypeMirror visitUnknown(AnnotationValue annotationValue, Void aVoid) {
        return null;
    }
}
