package net.sf.aidl2.internal.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

public final class TypeInvocation<E extends Element, T extends TypeMirror> {
    public final E element;

    public final T type;

    public TypeInvocation(E element, T type) {
        this.element = element;
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
