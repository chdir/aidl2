package net.sf.aidl2.internal.exceptions;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public final class ElementWarning implements FaultyElement {
    private final Element element;
    private final String message;

    public ElementWarning(Element element, String message) {
        this.element = element;
        this.message = message;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Diagnostic.Kind getKind() {
        return Diagnostic.Kind.WARNING;
    }

    @Override
    public int length() {
        return message.length();
    }

    @Override
    public char charAt(int i) {
        return message.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return message.subSequence(i, i1);
    }
}
