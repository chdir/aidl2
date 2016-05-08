// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import org.jetbrains.annotations.NotNull;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class NotNullParcelableParameter$$AidlClientImpl implements NotNullParcelableParameter {
    private final IBinder delegate;

    public NotNullParcelableParameter$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithNotNullParcelableParameter(@NotNull SimpleParcelable parcelableParam) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(NotNullParcelableParameter$$AidlServerImpl.DESCRIPTOR);

            parcelableParam.writeToParcel(data, 0);

            delegate.transact(NotNullParcelableParameter$$AidlServerImpl.TRANSACT_methodWithNotNullParcelableParameter, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
