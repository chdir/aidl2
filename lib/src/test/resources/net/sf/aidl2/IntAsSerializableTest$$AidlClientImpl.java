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
public final class IntAsSerializableTest$$AidlClientImpl implements IntAsSerializableTest {
    private final IBinder delegate;

    public IntAsSerializableTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithIntParameter(@As(DataKind.SERIALIZABLE) int parameter) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IntAsSerializableTest$$AidlServerImpl.DESCRIPTOR);

            AidlUtil.writeToObjectStream(data, parameter);

            delegate.transact(IntAsSerializableTest$$AidlServerImpl.TRANSACT_methodWithIntParameter, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}