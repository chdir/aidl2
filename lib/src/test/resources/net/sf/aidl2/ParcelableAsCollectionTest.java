package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ParcelableAsCollectionTest")
public interface ParcelableAsCollectionTest extends IInterface {
    @As(DataKind.SEQUENCE) ParcelableCollection methodWithCollectionReturn() throws RemoteException;
}
