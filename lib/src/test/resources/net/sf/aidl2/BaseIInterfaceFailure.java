package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.BaseIInterfaceFailure")
public interface BaseIInterfaceFailure extends IInterface {
    IInterface methodWithIInterfaceReturn() throws RemoteException;
}
