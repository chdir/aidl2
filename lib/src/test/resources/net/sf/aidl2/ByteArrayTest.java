package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ByteArrayTest")
public interface ByteArrayTest extends IInterface {
    byte[] methodWithByteArrayReturn() throws RemoteException;
}
