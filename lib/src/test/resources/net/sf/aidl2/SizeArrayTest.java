package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;
import android.util.Size;

@AIDL("net.sf.aidl2.SizeArrayTest")
public interface SizeArrayTest extends IInterface {
    Size[] methodWithSizeReturn() throws RemoteException;
}
