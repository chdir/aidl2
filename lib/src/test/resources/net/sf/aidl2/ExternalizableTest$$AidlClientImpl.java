// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
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
public final class ExternalizableTest$$AidlClientImpl implements ExternalizableTest {
    private final IBinder delegate;

    public ExternalizableTest$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithExternalizableParameter(SomeExternalizable externalizable) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ExternalizableTest$$AidlServerImpl.DESCRIPTOR);

            if (externalizable == null) {
                data.writeByte((byte) -1);
            } else {
                data.writeByte((byte) 0);
                ObjectOutputStream objectOutputStream = null;
                try {
                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    objectOutputStream = new ObjectOutputStream(arrayOutputStream);
                    externalizable.writeExternal(objectOutputStream);
                    data.writeByteArray(arrayOutputStream.toByteArray());
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to serialize net.sf.aidl2.SomeExternalizable", e);
                } finally {
                    AidlUtil.shut(objectOutputStream);
                }
            }

            delegate.transact(ExternalizableTest$$AidlServerImpl.TRANSACT_methodWithExternalizableParameter, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
