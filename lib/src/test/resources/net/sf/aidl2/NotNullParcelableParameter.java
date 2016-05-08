package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import org.jetbrains.annotations.NotNull;

@AIDL("net.sf.aidl2.NotNullParcelableParameter")
public interface NotNullParcelableParameter extends IInterface {
    void methodWithNotNullParcelableParameter(@NotNull SimpleParcelable parcelableParam) throws RemoteException;
}
