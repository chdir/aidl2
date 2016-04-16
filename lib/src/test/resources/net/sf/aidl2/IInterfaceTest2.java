package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.IInterfaceTest2")
public interface IInterfaceTest2 extends IInterface {
    RemoteApi methodWithCallbackReturn() throws RemoteException;
}
