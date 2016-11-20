// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.Externalizable;
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
public final class ExternalizableTest3$$AidlClientImpl implements ExternalizableTest3 {
    private final IBinder delegate;

    public ExternalizableTest3$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Externalizable methodReturningTheExternalizable() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ExternalizableTest3$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ExternalizableTest3$$AidlServerImpl.TRANSACT_methodReturningTheExternalizable, data, reply, 0);
            reply.readException();

            return AidlUtil.readFromObjectStream(reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
