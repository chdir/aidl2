package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface IncorrectTransactionId2 extends IInterface {
    @Call(Integer.MAX_VALUE)
    void test() throws RemoteException;
}
