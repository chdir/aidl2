package net.sf.aidl2.internal;

import com.squareup.javapoet.JavaFile;
import net.sf.aidl2.internal.exceptions.ElementException;

import java.io.IOException;
import java.util.List;

public interface AidlGenerator {
    void make(List<? super JavaFile> results, AidlModel aidlInterfaceDetails) throws ElementException, IOException;
}
