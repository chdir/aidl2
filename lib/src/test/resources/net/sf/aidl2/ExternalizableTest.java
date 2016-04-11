package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.ExternalizableTest")
public interface ExternalizableTest extends IInterface {
    void methodWithExternalizableParameter(SomeExternalizable externalizable) throws RemoteException;
}
