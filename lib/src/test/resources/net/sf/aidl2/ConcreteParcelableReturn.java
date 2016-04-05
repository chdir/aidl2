package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ConcreteParcelableReturn")
public interface ConcreteParcelableReturn extends IInterface {
    SimpleParcelable methodReturningParcelable() throws RemoteException;
}
