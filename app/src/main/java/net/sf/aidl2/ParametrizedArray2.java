package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ParametrizedArray2")
public interface ParametrizedArray2 extends IInterface {
    Parametrized<String>[] methodWithParametrizedArrayParam() throws RemoteException;
}
