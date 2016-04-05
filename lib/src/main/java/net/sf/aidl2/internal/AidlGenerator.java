package net.sf.aidl2.internal;

import java.io.IOException;

import javax.annotation.processing.Filer;

public interface AidlGenerator {
    void make(Filer filer) throws net.sf.aidl2.internal.exceptions.ElementException, IOException;
}
