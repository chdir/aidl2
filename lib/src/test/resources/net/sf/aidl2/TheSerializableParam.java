package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.Serializable;

@AIDL("net.sf.aidl2.TheSerializableParam")
public interface TheSerializableParam extends IInterface {
    void methodWithSerializableParameter(Serializable serializable) throws RemoteException;
}
