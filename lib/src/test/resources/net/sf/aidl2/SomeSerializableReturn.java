package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.Date;

@AIDL("net.sf.aidl2.SomeSerializableReturn")
public interface SomeSerializableReturn extends IInterface {
    Date methodReturningParcelable() throws RemoteException;
}
