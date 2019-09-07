package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ConcreteParcelableReturnTypevar")
public interface ConcreteParcelableReturnTypevar<T extends SimpleParcelable> extends IInterface {
    T methodReturningParcelable() throws RemoteException;
}
