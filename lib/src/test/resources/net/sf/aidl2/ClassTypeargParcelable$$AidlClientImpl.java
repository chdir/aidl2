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
public final class ClassTypeargParcelable$$AidlClientImpl<T extends Parcelable> implements ClassTypeargParcelable<T> {
    private final IBinder delegate;

    public ClassTypeargParcelable$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public T methodWithParcelableReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ClassTypeargParcelable$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ClassTypeargParcelable$$AidlServerImpl.TRANSACT_methodWithParcelableReturn, data, reply, 0);
            reply.readException();

            return reply.readParcelable(getClass().getClassLoader());
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
