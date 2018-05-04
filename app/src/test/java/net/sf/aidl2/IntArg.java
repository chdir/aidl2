package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.IntArg")
public interface IntArg extends IInterface {
    void methodWithIntParameter(int parameter) throws RemoteException;
}