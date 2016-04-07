package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.MethodTypeargParcelable")
public interface ClassTypeargParcelable<T extends Parcelable> extends IInterface {
    T methodWithParcelableReturn() throws RemoteException;
}
