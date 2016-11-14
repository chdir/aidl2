package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.StringTest")
public interface StringTest extends IInterface {
    void methodWithStringParameter(String stringParam) throws RemoteException;
}
