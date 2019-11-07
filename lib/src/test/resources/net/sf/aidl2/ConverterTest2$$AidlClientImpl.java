// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConverterTest2$$AidlClientImpl implements ConverterTest2 {
    private final IBinder delegate;

    private final ListConverter listConverter = new ListConverter();

    public ConverterTest2$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    @As(
            converter = ListConverter.class
    )
    public ArrayList<String> method() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConverterTest2$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ConverterTest2$$AidlServerImpl.TRANSACT_method, data, reply, 0);
            reply.readException();

            Type returnValueType = AidlUtil.of(ArrayList.class, String.class);
            return (ArrayList) listConverter.read(returnValueType, reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}