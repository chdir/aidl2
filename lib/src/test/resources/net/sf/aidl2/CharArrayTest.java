package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.CharArrayTest")
public interface CharArrayTest extends IInterface {
    char[][] methodWithBiCharArrayReturn() throws RemoteException;
}
