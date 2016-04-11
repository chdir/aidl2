package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.File;

@AIDL("net.sf.aidl2.SerializableArrayTest")
public interface SerializableArrayTest extends IInterface {
    void methodWithSerializableArrayParameter(File[] files) throws RemoteException;
}
