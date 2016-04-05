package net.sf.aidl2.internal.exceptions;

import org.jetbrains.annotations.NotNull;

public class ReadableException extends Exception implements CharSequence {
    public ReadableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadableException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public ReadableException(String message) {
        super(message);
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

    @Override @NotNull
    public String toString() {
        return getMessage();
    }
}
