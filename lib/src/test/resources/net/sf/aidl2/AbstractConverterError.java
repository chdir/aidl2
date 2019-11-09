package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface AbstractConverterError extends IInterface {
    void methodWithIntParameter(@As(converter = AbstractConverter.class) int parameter) throws RemoteException;
}
