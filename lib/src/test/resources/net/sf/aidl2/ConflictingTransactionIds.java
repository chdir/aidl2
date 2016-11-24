package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface ConflictingTransactionIds extends IInterface {
    @Call(143)
    String o() throws RemoteException;

    @Call(143)
    String o(Integer intParam) throws RemoteException;
}
