package net.sf.aidl2;

import android.os.*;

// XXX: this does not seem to work due to bug in javac
public interface Recursive3 extends IInterface {
    /*
    <U extends Parametrized3<? extends T>, T extends Parametrized3<? extends U>> void wow(T trouble) throws RemoteException;
    */
}
