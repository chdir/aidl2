package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.UnknownTypeFailure")
public interface UnknownTypeFailure extends IInterface {
    void methodWithWeirdParameter(Runnable nonSerializableType) throws RemoteException;
}
