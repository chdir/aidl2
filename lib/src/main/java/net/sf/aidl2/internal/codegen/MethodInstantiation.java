package net.sf.aidl2.internal.codegen;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

public final class MethodInstantiation {
    public final ExecutableElement element;

    public final ExecutableType type;

    public MethodInstantiation(ExecutableElement element, ExecutableType type) {
        this.element = element;
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
