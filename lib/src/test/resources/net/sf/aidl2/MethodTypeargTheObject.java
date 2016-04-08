package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL("net.sf.aidl2.MethodTypeargTheObject")
public interface MethodTypeargTheObject extends IInterface {
    @SuppressWarnings("unchecked")
    <T> void methodWithObjectParameter(T object) throws RemoteException;
}
