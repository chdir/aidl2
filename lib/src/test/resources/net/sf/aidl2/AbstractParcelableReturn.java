package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.AbstractParcelableReturn")
public interface AbstractParcelableReturn extends IInterface {
    Parcelable methodReturningParcelable() throws RemoteException;
}
