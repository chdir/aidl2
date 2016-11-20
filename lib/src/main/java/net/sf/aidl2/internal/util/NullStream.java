package net.sf.aidl2.internal.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public final class NullStream extends OutputStream {
    public NullStream() {
    }

    public void write(int b) throws IOException {
    }

    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(@NotNull byte[] b, int off, int len) throws IOException {
    }

    public void write(@NotNull byte[] b) throws IOException {
    }
}
