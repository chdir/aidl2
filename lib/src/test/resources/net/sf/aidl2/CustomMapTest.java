package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface CustomMapTest extends IInterface {
    DumbMap mapTest(DumbMap[] dumbMap) throws RemoteException;
}
