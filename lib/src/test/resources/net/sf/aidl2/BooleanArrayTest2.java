package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.BooleanArrayTest2")
public interface BooleanArrayTest2 extends IInterface {
    boolean[] methodWithBooleanArrayReturn() throws RemoteException;
}
