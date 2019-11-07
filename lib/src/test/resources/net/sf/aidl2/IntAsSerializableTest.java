package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.IntAsSerializableTest")
public interface IntAsSerializableTest extends IInterface {
    void methodWithIntParameter(@As(DataKind.SERIALIZABLE) int parameter) throws RemoteException;
}
