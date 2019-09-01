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
public final class FloatArrayTest$$AidlClientImpl implements FloatArrayTest {
    private final IBinder delegate;

    public FloatArrayTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public float[] methodWithFloatArrayReturnValue() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(FloatArrayTest$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(FloatArrayTest$$AidlServerImpl.TRANSACT_methodWithFloatArrayReturnValue, data, reply, 0);
            reply.readException();

            return reply.createFloatArray();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
