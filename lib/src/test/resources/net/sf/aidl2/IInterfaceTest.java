package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.IInterfaceTest")
public interface IInterfaceTest extends IInterface {
    void methodWithCallbackParameter(RemoteApi api) throws RemoteException;
}
