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
public final class IntArrayTest$$AidlClientImpl implements IntArrayTest {
    private final IBinder delegate;

    public IntArrayTest$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithIntArrayParam(int[] array) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IntArrayTest$$AidlServerImpl.DESCRIPTOR);

            data.writeIntArray(array);

            delegate.transact(IntArrayTest$$AidlServerImpl.TRANSACT_methodWithIntArrayParam, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
