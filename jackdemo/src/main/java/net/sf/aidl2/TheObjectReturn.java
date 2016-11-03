package net.sf.aidl2;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.TheObjectReturn")
public interface TheObjectReturn extends IInterface {
    @SuppressWarnings("unchecked")
    Object methodWithObjectReturn() throws RemoteException;

    @Override
    IBinder asBinder();
}
