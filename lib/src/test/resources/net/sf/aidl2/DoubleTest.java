package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.DoubleTest")
public interface DoubleTest extends IInterface {
    void methodWithDoubleParameter(double parameter) throws RemoteException;
}
