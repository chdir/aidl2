package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface RecursiveType3 extends IInterface {
    <T extends Parametrized3<T, Parametrized3<T, Object, ?>, ?>> Parametrized3<T, Object, ?> wow() throws RemoteException;
}
