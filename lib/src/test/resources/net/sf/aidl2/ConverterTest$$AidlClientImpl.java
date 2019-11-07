// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.FileOutputStream;
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
public final class ConverterTest$$AidlClientImpl implements ConverterTest {
    private final IBinder delegate;

    private final StreamConverter streamConverter = new StreamConverter();

    public ConverterTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void method(@As(converter = StreamConverter.class) FileOutputStream arg) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConverterTest$$AidlServerImpl.DESCRIPTOR);

            streamConverter.write(arg, data);

            delegate.transact(ConverterTest$$AidlServerImpl.TRANSACT_method, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}