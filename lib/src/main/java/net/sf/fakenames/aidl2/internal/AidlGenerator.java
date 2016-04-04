package net.sf.fakenames.aidl2.internal;

import net.sf.fakenames.aidl2.internal.exceptions.ElementException;

import java.io.IOException;

import javax.annotation.processing.Filer;

public interface AidlGenerator {
    void make(Filer filer) throws ElementException, IOException;
}
