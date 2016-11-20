// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.File;
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
public final class SerializableArrayTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.SerializableArrayTest";

    static final int TRANSACT_methodWithSerializableArrayParameter = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final SerializableArrayTest delegate;

    public SerializableArrayTest$$AidlServerImpl(SerializableArrayTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithSerializableArrayParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final File[] files = AidlUtil.readFromObjectStream(data);

                delegate.methodWithSerializableArrayParameter(files);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
