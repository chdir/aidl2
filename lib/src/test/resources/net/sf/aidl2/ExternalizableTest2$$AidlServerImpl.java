// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectOutputStream;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ExternalizableTest2$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ExternalizableTest2";

    static final int TRANSACT_methodWithExternalizableReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final ExternalizableTest2 delegate;

    public ExternalizableTest2$$AidlServerImpl(ExternalizableTest2 delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithExternalizableReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                Externalizable returnValue = this.delegate.methodWithExternalizableReturn();
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeByte((byte) -1);
                } else {
                    reply.writeByte((byte) 0);
                    ObjectOutputStream objectOutputStream = null;
                    try {
                        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                        objectOutputStream = new ObjectOutputStream(arrayOutputStream);
                        returnValue.writeExternal(objectOutputStream);
                        reply.writeByteArray(arrayOutputStream.toByteArray());
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to serialize net.sf.aidl2.SomeExternalizable", e);
                    } finally {
                        AidlUtil.shut(objectOutputStream);
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
