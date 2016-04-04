package net.sf.fakenames.aidl2.internal.exceptions;

public class QuietException extends ReadableException {
    public QuietException(String s) {
        super(s);
    }

    @Override
    public void printStackTrace() {
        // no-op, since this exception is not detail-savvy
    }
}
