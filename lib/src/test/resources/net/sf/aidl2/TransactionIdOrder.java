package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL
@SuppressWarnings("unchecked")
public interface TransactionIdOrder extends IInterface, Parent1, Parent2 {
    void yetAnotherMethod(String parameter) throws RemoteException;

    Integer anotherMethod(long parameter) throws RemoteException;

    Parcelable someMethod() throws RemoteException;

    Parcelable someMethod(byte byteParameter) throws RemoteException;

    Parcelable someMethod(int[] intArrayParam) throws RemoteException;
}
