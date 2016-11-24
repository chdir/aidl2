package net.sf.aidl2;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL
@SuppressWarnings("unchecked")
public interface AdjustingTransactionIds extends IInterface, Parent1, Parent2 {
    @Call(14)
    void yetAnotherMethod(String parameter) throws RemoteException;

    Integer anotherMethod(long parameter) throws RemoteException;

    @SuppressWarnings("aidl2")
    @Call(IBinder.LIKE_TRANSACTION)
    Parcelable someMethod() throws RemoteException;

    @Call(2)
    Parcelable someMethod(byte byteParameter) throws RemoteException;

    @Call(1)
    String o() throws RemoteException;

    @Call(AidlUtil.VERSION_TRANSACTION)
    Parcelable someMethod(int[] intArrayParam) throws RemoteException;
}
