package net.sf.aidl2.internal.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public final class TypeInvocation<E extends Element, T extends TypeMirror> {
    public final E element;

    public final T type;

    public TypeInvocation(E element, T type) {
        this.element = element;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public TypeInvocation<E, T> refine(Types types, DeclaredType type) {
        return new TypeInvocation<>(element, (T) types.asMemberOf(type, element));
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
