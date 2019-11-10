// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
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
public final class BooleanArrayTest2$$AidlClientImpl implements BooleanArrayTest2 {
    private final IBinder delegate;

    public BooleanArrayTest2$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public boolean[] methodWithBooleanArrayReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(BooleanArrayTest2$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(BooleanArrayTest2$$AidlServerImpl.TRANSACT_methodWithBooleanArrayReturn, data, reply, 0);
            reply.readException();

            return reply.createBooleanArray();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}