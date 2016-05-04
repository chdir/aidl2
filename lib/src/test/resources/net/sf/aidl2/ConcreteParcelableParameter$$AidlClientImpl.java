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
public final class ConcreteParcelableParameter$$AidlClientImpl implements ConcreteParcelableParameter {
    private final IBinder delegate;

    public ConcreteParcelableParameter$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodReturningParcelable(SimpleParcelable parcelableParameter) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteParcelableParameter$$AidlServerImpl.DESCRIPTOR);

            if (parcelableParameter == null) {
                data.writeByte((byte) -1);
            } else {
                data.writeByte((byte) 0);
                parcelableParameter.writeToParcel(data, 0);
            }

            delegate.transact(ConcreteParcelableParameter$$AidlServerImpl.TRANSACT_methodReturningParcelable, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
