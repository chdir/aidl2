package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.IntArrayTest")
public interface IntArrayTest extends IInterface {
    void methodWithIntArrayParam(int[] array) throws RemoteException;
}
