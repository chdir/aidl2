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
public final class ConverterTest3$$AidlClientImpl implements ConverterTest3 {
    private final IBinder delegate;

    private final NumberConverter numberConverter = new NumberConverter();

    public ConverterTest3$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    @As(
            converter = NumberConverter.class
    )
    public int method() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConverterTest3$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ConverterTest3$$AidlServerImpl.TRANSACT_method, data, reply, 0);
            reply.readException();

            return (int) numberConverter.read(int.class, reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}