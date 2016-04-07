package net.sf.fakenames.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import net.sf.aidl2.AIDL;

@AIDL("net.sf.aidl2.IntTest3")
public interface IntTest3 extends IInterface {
    void methodWithIntVararg(int... ints) throws RemoteException;
}
