package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface RecursiveType2 extends IInterface {
    <U extends Parametrized<? extends T>, T extends Parametrized<? extends U>> T wow() throws RemoteException;
}
