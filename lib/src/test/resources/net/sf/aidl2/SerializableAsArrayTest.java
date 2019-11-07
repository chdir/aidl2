package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.Date;

@AIDL("net.sf.aidl2.SerializableAsArrayTest")
public interface SerializableAsArrayTest extends IInterface {
    void methodWithSerializableArrayParameter(@As(DataKind.SEQUENCE) Date[] parameter) throws RemoteException;
}
