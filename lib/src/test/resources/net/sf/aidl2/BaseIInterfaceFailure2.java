package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.BaseIInterfaceFailure2")
public interface BaseIInterfaceFailure2 extends IInterface {
    void methodWithIInterfaceReturn(IInterface parameter) throws RemoteException;
}
