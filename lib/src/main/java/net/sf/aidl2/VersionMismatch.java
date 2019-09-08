package net.sf.aidl2;

import android.os.RemoteException;

/**
 * This exception is thrown by {@link InterfaceLoader#asInterface InterfaceLoader.asInterface()} when the version of IPC interface
 * advertised by remote process differs from the version of locally available RPC proxy. Version difference
 * signifies major changes in binary protocol, that can not be reconciled or worked around. This usually means,
 * that client and server use different versions of the library and one of them should be updated.
 * <p>
 *
 * This exception can never be thrown if the local RPC proxy lacks versioning information.
 */
public final class VersionMismatch extends RemoteException {
    public VersionMismatch(String errorMessage) {
        super(errorMessage);
    }
}
