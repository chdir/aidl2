package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface IncorrectTransactionId extends IInterface {
    @Call(-2)
    void test() throws RemoteException;
}
