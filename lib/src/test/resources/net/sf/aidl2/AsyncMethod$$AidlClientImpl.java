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
public final class AsyncMethod$$AidlClientImpl implements AsyncMethod {
    private final IBinder delegate;

    public AsyncMethod$$AidlClientImpl(IBinder delegate)  throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    @OneWay
    public void oneWay() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = null;
        try {
            data.writeInterfaceToken(AsyncMethod$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(AsyncMethod$$AidlServerImpl.TRANSACT_oneWay, data, reply, IBinder.FLAG_ONEWAY);
        } finally {
            data.recycle();
        }
    }
}
