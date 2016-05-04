// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
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
public final class ExternalizableTest2$$AidlClientImpl implements ExternalizableTest2 {
    private final IBinder delegate;

    public ExternalizableTest2$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public SomeExternalizable methodWithExternalizableReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ExternalizableTest2$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ExternalizableTest2$$AidlServerImpl.TRANSACT_methodWithExternalizableReturn, data, reply, 0);
            reply.readException();

            final SomeExternalizable returnValueTmp;
            if (reply.readByte() == -1) {
                returnValueTmp = null;
            } else {
                ObjectInputStream objectInputStream = null;
                SomeExternalizable returnValueExternalizable = null;
                try {
                    objectInputStream = new ObjectInputStream(new ByteArrayInputStream(reply.createByteArray()));
                    returnValueExternalizable = new SomeExternalizable();
                    returnValueExternalizable.readExternal(objectInputStream);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to deserialize net.sf.aidl2.SomeExternalizable", e);
                } finally {
                    AidlUtil.shut(objectInputStream);
                }
                returnValueTmp = returnValueExternalizable;
            }
            return returnValueTmp;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}