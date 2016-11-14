package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.DoubleTest2")
public interface DoubleTest2 extends IInterface {
    double methodWithDoubleReturn() throws RemoteException;
}
