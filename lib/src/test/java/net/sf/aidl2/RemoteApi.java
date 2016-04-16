package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("RemoteApi")
public interface RemoteApi extends IInterface {
    String test() throws RemoteException;
}
