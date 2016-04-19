// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.IllegalArgumentException;
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
public final class ExtendingInterface$$AidlClientImpl implements ExtendingInterface {
    private final IBinder delegate;

    public ExtendingInterface$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Date call() throws RemoteException, IllegalArgumentException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ExtendingInterface$$AidlServerImpl.DESCRIPTOR);

            this.delegate.transact(ExtendingInterface$$AidlServerImpl.TRANSACT_call, data, reply, 0);
            reply.readException();

            return AidlUtil.readSafeSerializable(reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
