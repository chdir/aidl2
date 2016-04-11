package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.VoidTest3")
public interface VoidTest3 extends IInterface {
    void methodWithBiCharArrayReturn(Void... voidVararg) throws RemoteException;
}
