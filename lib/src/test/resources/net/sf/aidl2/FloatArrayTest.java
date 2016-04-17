package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.FloatArrayTest")
public interface FloatArrayTest extends IInterface {
    float[] methodWithFloatArrayReturnValue() throws RemoteException;
}
