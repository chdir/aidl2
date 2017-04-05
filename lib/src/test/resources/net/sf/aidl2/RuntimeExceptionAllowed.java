package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface RuntimeExceptionAllowed extends IInterface {
    void aMethod() throws IllegalArgumentException, RemoteException;
}
