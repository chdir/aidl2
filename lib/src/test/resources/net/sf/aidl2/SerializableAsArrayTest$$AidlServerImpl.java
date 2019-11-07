// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.Date;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class SerializableAsArrayTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.SerializableAsArrayTest";

    static final int TRANSACT_methodWithSerializableArrayParameter = IBinder.FIRST_CALL_TRANSACTION;

    private final SerializableAsArrayTest delegate;

    public SerializableAsArrayTest$$AidlServerImpl(SerializableAsArrayTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithSerializableArrayParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Date[] parameterArray;
                final int parameterLength = data.readInt();
                if (parameterLength < 0) {
                    parameterArray = null;
                } else {
                    parameterArray = new Date[parameterLength];
                    for (int i = 0; i < parameterArray.length; i++) {
                        parameterArray[i] = AidlUtil.readFromObjectStream(data);
                    }
                }

                delegate.methodWithSerializableArrayParameter(parameterArray);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}