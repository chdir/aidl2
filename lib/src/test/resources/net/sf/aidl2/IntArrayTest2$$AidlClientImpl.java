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
public final class IntArrayTest2$$AidlClientImpl implements IntArrayTest2 {
    private final IBinder delegate;

    public IntArrayTest2$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public int[] methodWithIntArrayReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IntArrayTest2$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(IntArrayTest2$$AidlServerImpl.TRANSACT_methodWithIntArrayReturn, data, reply, 0);
            reply.readException();

            return reply.createIntArray();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
