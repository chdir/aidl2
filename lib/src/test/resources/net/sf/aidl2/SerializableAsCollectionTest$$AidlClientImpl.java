// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Date;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class SerializableAsCollectionTest$$AidlClientImpl implements SerializableAsCollectionTest {
    private final IBinder delegate;

    public SerializableAsCollectionTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithSerializableCollectionParameter(@As(DataKind.SEQUENCE) ArrayList<Date> parameter) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SerializableAsCollectionTest$$AidlServerImpl.DESCRIPTOR);

            if (parameter == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(parameter.size());
                for (Serializable parameterElement : parameter) {
                    AidlUtil.writeToObjectStream(data, parameterElement);
                }
            }

            delegate.transact(SerializableAsCollectionTest$$AidlServerImpl.TRANSACT_methodWithSerializableCollectionParameter, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}