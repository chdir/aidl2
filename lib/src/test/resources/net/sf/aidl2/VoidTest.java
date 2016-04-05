package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.VoidTest")
public interface VoidTest extends IInterface {
    Void methodWithVoidReturn() throws RemoteException;
}
