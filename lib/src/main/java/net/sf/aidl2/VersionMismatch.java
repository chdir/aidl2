package net.sf.aidl2;

import android.os.RemoteException;

public final class VersionMismatch extends RemoteException {
    public VersionMismatch(String errorMessage) {
        super(errorMessage);
    }
}
