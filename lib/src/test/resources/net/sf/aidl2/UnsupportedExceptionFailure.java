package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.IOException;

@AIDL(value = "net.sf.aidl2.UnsupportedExceptionFailure")
public interface UnsupportedExceptionFailure extends IInterface {
    void methodThrowingIOException() throws RemoteException, IOException;
}
