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
public final class ConcreteParcelableReturnTypevar$$AidlClientImpl<T extends SimpleParcelable> implements ConcreteParcelableReturnTypevar<T> {
    private final IBinder delegate;

    public ConcreteParcelableReturnTypevar$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public T methodReturningParcelable() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteParcelableReturnTypevar$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ConcreteParcelableReturnTypevar$$AidlServerImpl.TRANSACT_methodReturningParcelable, data, reply, 0);
            reply.readException();

            final SimpleParcelable returnValueTmp;
            if (reply.readByte() == -1) {
                returnValueTmp = null;
            } else {
                returnValueTmp = SimpleParcelable.CREATOR.createFromParcel(reply);
            }
            return (T) returnValueTmp;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}