package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.FileOutputStream;

@AIDL("net.sf.aidl2.ConverterTest")
public interface ConverterTest extends IInterface {
    void method(@As(converter = StreamConverter.class) FileOutputStream arg) throws RemoteException;
}
