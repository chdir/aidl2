package net.sf.fakenames.aidl2.internal.exceptions;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * An exception, associated with specific source code element.
 */
public final class ElementException extends ReadableException implements FaultyElement {
    private Element element;

    public ElementException(String message, Element element) {
        super(message);

        this.element = element;
    }

    public ElementException(ReadableException e, Element element) {
        super(e);

        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public Diagnostic.Kind getKind() {
        return Diagnostic.Kind.ERROR;
    }

    @Override
    public int length() {
        return getMessage().length();
    }

    @Override
    public char charAt(int i) {
        return getMessage().charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return getMessage().subSequence(i, i1);
    }
}
