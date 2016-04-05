package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.AbstractParcelableParameter")
public interface AbstractParcelableParameter extends IInterface {
    void methodWithParcelableParameter(Parcelable parcelableParameter) throws RemoteException;
}
