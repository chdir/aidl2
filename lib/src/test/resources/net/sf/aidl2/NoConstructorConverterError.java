package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface NoConstructorConverterError extends IInterface {
    void methodWithIntParameter(@As(converter = NoConstructorConverter.class) int parameter) throws RemoteException;
}
