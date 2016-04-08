package net.sf.aidl2;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.BundleTest")
public interface BundleTest extends IInterface {
    Bundle methodWithBundleParameter(Bundle bundle) throws RemoteException;
}
