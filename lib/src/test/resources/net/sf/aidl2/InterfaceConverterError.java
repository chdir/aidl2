package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface InterfaceConverterError extends IInterface {
    void methodWithIntParameter(@As(converter = Converter.class) int parameter) throws RemoteException;
}
