// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
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
public final class AbstractParcelableReturn$$AidlClientImpl implements AbstractParcelableReturn {
    private final IBinder delegate;

    public AbstractParcelableReturn$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Parcelable methodReturningParcelable() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(AbstractParcelableReturn$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(AbstractParcelableReturn$$AidlServerImpl.TRANSACT_methodReturningParcelable, data, reply, 0);
            reply.readException();

            return reply.readParcelable(getClass().getClassLoader());
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}