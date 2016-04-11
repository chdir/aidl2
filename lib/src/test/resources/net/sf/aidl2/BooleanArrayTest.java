package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.BooleanArrayTest")
public interface BooleanArrayTest<B extends Boolean> extends IInterface {
    void methodWithBiCharArrayReturn(B[][][] booleanArrayParam) throws RemoteException;
}
