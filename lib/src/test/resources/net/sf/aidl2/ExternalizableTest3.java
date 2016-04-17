package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.Externalizable;

@AIDL("net.sf.aidl2.ExternalizableTest3")
public interface ExternalizableTest3 extends IInterface {
    Externalizable methodReturningTheExternalizable() throws RemoteException;
}