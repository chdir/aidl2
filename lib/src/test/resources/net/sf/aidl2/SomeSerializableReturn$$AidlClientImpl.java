// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.util.Date;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class SomeSerializableReturn$$AidlClientImpl implements SomeSerializableReturn {
    private final IBinder delegate;

    public SomeSerializableReturn$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Date methodReturningParcelable() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SomeSerializableReturn$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(SomeSerializableReturn$$AidlServerImpl.TRANSACT_methodReturningParcelable, data, reply, 0);
            reply.readException();

            return AidlUtil.readFromObjectStream(reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
