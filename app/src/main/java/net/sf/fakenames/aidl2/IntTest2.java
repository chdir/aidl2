package net.sf.fakenames.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import net.sf.aidl2.AIDL;

@AIDL
public interface IntTest2 extends IInterface {
    int methodWithIntReturn() throws RemoteException;
}
