package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import non.existing.packa.ge.NonExistingType;

@AIDL("net.sf.aidl2.MissingTypeFailure")
public interface MissingTypeFailure extends IInterface {
    NonExistingType methodWithMissingReturnType() throws RemoteException;
}
