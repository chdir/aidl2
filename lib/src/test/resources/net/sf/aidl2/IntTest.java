package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.IntTest")
public interface IntTest extends IInterface {
    void methodWithIntParameter(int parameter) throws RemoteException;
}
