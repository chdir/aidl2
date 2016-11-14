package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.StringTest2")
public interface StringTest2 extends IInterface {
    String methodWithStringReturn() throws RemoteException;
}
