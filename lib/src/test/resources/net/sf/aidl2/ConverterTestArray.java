package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.FileOutputStream;
import java.util.ArrayList;

@AIDL("net.sf.aidl2.ConverterTestArray")
public interface ConverterTestArray extends IInterface {
    @As(converter = ArrayConverter.class) String[][] method() throws RemoteException;
}
