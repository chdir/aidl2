package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL
public interface Parent2 extends Parent3 {
    Parcelable someMethod(char charParam) throws RemoteException;

    Parcelable someMethod(byte byteParam) throws RemoteException;

    Parcelable someMethod(long intArrayParam) throws RemoteException;

    Parcelable someMethod2() throws RemoteException;

    Integer anotherMethod(long parameter) throws RemoteException;

    void yetAnotherMethod() throws RemoteException;
}
