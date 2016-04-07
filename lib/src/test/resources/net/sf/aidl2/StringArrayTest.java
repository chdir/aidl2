package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.StringArrayTest")
public interface StringArrayTest extends IInterface {
    String[] methodWithStringReturn() throws RemoteException;
}
