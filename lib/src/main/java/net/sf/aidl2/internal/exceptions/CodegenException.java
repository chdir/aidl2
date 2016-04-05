package net.sf.aidl2.internal.exceptions;

/**
 * "Faceless" exception, not tied to any element in source code. Avoid.
 */
public final class CodegenException extends ReadableException {
    public CodegenException(String message) {
        super(message);
    }
}
