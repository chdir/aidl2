package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface AsAnnotationOnVoidError extends IInterface {
    @As(converter = ObjectConverter.class)
    void methodWithIntParameter(int parameter) throws RemoteException;
}
