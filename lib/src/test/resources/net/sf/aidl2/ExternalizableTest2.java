package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ExternalizableTest2")
public interface ExternalizableTest2 extends IInterface {
    SomeExternalizable methodWithExternalizableReturn() throws RemoteException;
}