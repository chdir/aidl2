package net.sf.aidl2.internal.exceptions;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public interface FaultyElement extends CharSequence {
    Element getElement();

    String getMessage();

    Diagnostic.Kind getKind();
}
