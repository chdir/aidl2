package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.IntTest2")
public interface IntTest2 extends IInterface {
    int methodWithIntReturn() throws RemoteException;
}
