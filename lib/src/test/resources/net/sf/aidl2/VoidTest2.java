package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.VoidTest2")
public interface VoidTest2 extends IInterface {
    void methodWithVoidParameter(Void ignored) throws RemoteException;
}
