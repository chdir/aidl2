package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.Date;

@AIDL("net.sf.aidl2.SomeSerializableParam")
public interface SomeSerializableParam extends IInterface {
    void methodWithSerializableParameter(Date serializable) throws RemoteException;
}
