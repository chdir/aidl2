// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Date;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class SerializableAsCollectionTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.SerializableAsCollectionTest";

    static final int TRANSACT_methodWithSerializableCollectionParameter = IBinder.FIRST_CALL_TRANSACTION;

    private final SerializableAsCollectionTest delegate;

    public SerializableAsCollectionTest$$AidlServerImpl(SerializableAsCollectionTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithSerializableCollectionParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final ArrayList<Date> parameterCollection;
                final int parameterSize = data.readInt();
                if (parameterSize < 0) {
                    parameterCollection = null;
                } else {
                    parameterCollection = new ArrayList<>(parameterSize);
                    for (int j = 0; j < parameterSize; j++) {
                        parameterCollection.add(AidlUtil.readFromObjectStream(data));
                    }
                }

                delegate.methodWithSerializableCollectionParameter(parameterCollection);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}