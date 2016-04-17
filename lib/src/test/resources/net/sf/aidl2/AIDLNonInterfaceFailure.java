package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.AIDLNonInterfaceFailure")
public @interface AIDLNonInterfaceFailure extends IInterface {
}
