package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.AsyncMethod")
public interface AsyncMethod extends IInterface {
    @OneWay
    void oneWay() throws RemoteException;
}
