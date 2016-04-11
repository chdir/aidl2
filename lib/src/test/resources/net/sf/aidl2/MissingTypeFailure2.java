package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import non.existing.packa.ge.NonExistingType;

@AIDL("net.sf.aidl2.MissingTypeFailure2")
public interface MissingTypeFailure2 extends IInterface {
    void methodWithMissingReturnType(NonExistingType wrongParameter) throws RemoteException;
}
