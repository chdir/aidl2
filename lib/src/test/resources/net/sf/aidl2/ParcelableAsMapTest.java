package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ParcelableAsMapTest")
public interface ParcelableAsMapTest extends IInterface {
    @As(DataKind.MAP) ParcelableMap methodWithMapReturn() throws RemoteException;
}
