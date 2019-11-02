package net.sf.aidl2.internal;

import net.sf.aidl2.DataKind;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

final class ArgKindVisitor extends SimpleAnnotationValueVisitor6<DataKind, Void> {
    @Override
    public DataKind visitEnumConstant(VariableElement variableElement, Void aVoid) {
        Name argName = variableElement.getSimpleName();

        try {
            return DataKind.valueOf(argName.toString());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
}
