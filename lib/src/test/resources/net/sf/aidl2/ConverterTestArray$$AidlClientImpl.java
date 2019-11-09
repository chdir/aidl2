// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.lang.reflect.Type;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConverterTestArray$$AidlClientImpl implements ConverterTestArray {
    private final IBinder delegate;

    private final ArrayConverter arrayConverter = new ArrayConverter();

    public ConverterTestArray$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    @As(
            converter = ArrayConverter.class
    )
    public String[][] method() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConverterTestArray$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ConverterTestArray$$AidlServerImpl.TRANSACT_method, data, reply, 0);
            reply.readException();

            Type returnValueType = String[][].class;
            return arrayConverter.read(returnValueType, reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}