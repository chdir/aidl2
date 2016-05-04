// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.Runnable;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MethodTypeargParcelable$$AidlClientImpl implements MethodTypeargParcelable {
    private final IBinder delegate;

    public MethodTypeargParcelable$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public <T extends Runnable & Parcelable> void methodWithParcelableParam(T parcelable) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MethodTypeargParcelable$$AidlServerImpl.DESCRIPTOR);

            data.writeParcelable(parcelable, 0);

            delegate.transact(MethodTypeargParcelable$$AidlServerImpl.TRANSACT_methodWithParcelableParam, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
