package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ParametrizedArray")
public interface ParametrizedArray extends IInterface {
    void methodWithParametrizedArrayParam(Parametrized<String>[] parametrizedArray) throws RemoteException;
}
