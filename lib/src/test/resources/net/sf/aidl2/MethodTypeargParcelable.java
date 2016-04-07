package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.MethodTypeargParcelable")
public interface MethodTypeargParcelable extends IInterface {
    <T extends Runnable & Parcelable> void methodWithParcelableParam(T parcelable) throws RemoteException;
}
