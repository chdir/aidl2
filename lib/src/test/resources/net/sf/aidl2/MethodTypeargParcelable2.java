package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.MethodTypeargParcelable2")
public interface MethodTypeargParcelable2 extends IInterface {
    <T extends Parcelable> T methodWithParcelableParam() throws RemoteException;
}
