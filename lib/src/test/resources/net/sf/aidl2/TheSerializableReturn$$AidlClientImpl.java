// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Override;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class TheSerializableReturn$$AidlClientImpl implements TheSerializableReturn {
    private final IBinder delegate;

    public TheSerializableReturn$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Serializable methodReturningParcelable() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(TheSerializableReturn$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(TheSerializableReturn$$AidlServerImpl.TRANSACT_methodReturningParcelable, data, reply, 0);
            reply.readException();

            return AidlUtil.readFromObjectStream(reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
