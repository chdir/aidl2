package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface WeirdCollectionFailure extends IInterface {
    <T extends WeirdCollection> T[] methodWithWeirdCollectionReturn() throws RemoteException;
}
