package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ParametrizedSerialArray")
public interface ParametrizedSerialArray extends IInterface {
    void methodWithParametrizedArrayParam(Parametrized2<String>[] parametrizedArray) throws RemoteException;
}
