package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.RawObjectNotSuppressedFailure")
public interface RawObjectNotSuppressedFailure extends IInterface {
    Object methodWithBundleParameter() throws RemoteException;
}
