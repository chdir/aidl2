package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface ServiceApi extends IInterface {
    String test() throws RemoteException;
}
