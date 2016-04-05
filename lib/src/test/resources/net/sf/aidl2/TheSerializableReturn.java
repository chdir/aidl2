package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.Serializable;

@AIDL("net.sf.aidl2.TheSerializableReturn")
public interface TheSerializableReturn extends IInterface {
    Serializable methodReturningParcelable() throws RemoteException;
}
