package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL(value = "net.sf.aidl2.InsecureAidl2", insecure = true)
public interface InsecureAidl2 extends IInterface {
    void insecureMethod() throws RemoteException;
}
