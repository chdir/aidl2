package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ExampleCustomDescriptor")
public interface CustomDescriptor extends IInterface {
    void method() throws RemoteException;
}
