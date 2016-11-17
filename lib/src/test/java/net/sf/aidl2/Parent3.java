package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL
public interface Parent3 extends IInterface {
    Parcelable someMethod3(@SuppressWarnings("unchecked") Object[] test) throws RemoteException;

    <T> Parcelable someMethod4(@SuppressWarnings("unchecked") T[] test) throws RemoteException;

    Parcelable someMethod() throws RemoteException;
}
