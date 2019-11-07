package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.FileOutputStream;
import java.util.ArrayList;

@AIDL("net.sf.aidl2.ConverterTest3")
public interface ConverterTest3 extends IInterface {
    @As(converter = NumberConverter.class) int method() throws RemoteException;
}
