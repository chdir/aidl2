package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Date;

@AIDL("net.sf.aidl2.SerializableAsCollectionTest")
public interface SerializableAsCollectionTest extends IInterface {
    void methodWithSerializableCollectionParameter(@As(DataKind.SEQUENCE) ArrayList<Date> parameter) throws RemoteException;
}
