package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.FileOutputStream;
import java.util.ArrayList;

@AIDL("net.sf.aidl2.ConverterTest2")
public interface ConverterTest2 extends IInterface {
    @As(converter = ListConverter.class) ArrayList<String> method() throws RemoteException;
}
