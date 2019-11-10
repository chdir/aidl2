// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.util.Date;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class SerializableAsArrayTest$$AidlClientImpl implements SerializableAsArrayTest {
    private final IBinder delegate;

    public SerializableAsArrayTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithSerializableArrayParameter(@As(DataKind.SEQUENCE) Date[] parameter) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SerializableAsArrayTest$$AidlServerImpl.DESCRIPTOR);

            if (parameter == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(parameter.length);
                for (Date parameterComponent : parameter) {
                    AidlUtil.writeToObjectStream(data, parameterComponent);
                }
            }

            delegate.transact(SerializableAsArrayTest$$AidlServerImpl.TRANSACT_methodWithSerializableArrayParameter, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}