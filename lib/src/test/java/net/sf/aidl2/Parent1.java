package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL
public interface Parent1 extends IInterface {
    void yetAnotherMethod(String parameter, String anotherParameter) throws RemoteException;

    void yetAnotherMethod(String parameter) throws RemoteException;

    Parcelable someMethod() throws RemoteException;
}
