// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
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
public final class MethodTypeargParcelable2$$AidlClientImpl implements MethodTypeargParcelable2 {
    private final IBinder delegate;

    public MethodTypeargParcelable2$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public <T extends Parcelable> T methodWithParcelableParam() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MethodTypeargParcelable2$$AidlServerImpl.DESCRIPTOR);

            this.delegate.transact(MethodTypeargParcelable2$$AidlServerImpl.TRANSACT_methodWithParcelableParam, data, reply, 0);
            reply.readException();

            return AidlUtil.unsafeCast(reply.readParcelable(getClass().getClassLoader()));
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
