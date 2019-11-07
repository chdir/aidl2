// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.Map;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ParcelableAsMapTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ParcelableAsMapTest";

    static final int TRANSACT_methodWithMapReturn = IBinder.FIRST_CALL_TRANSACTION;

    private final ParcelableAsMapTest delegate;

    public ParcelableAsMapTest$$AidlServerImpl(ParcelableAsMapTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithMapReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Map<Integer, String> returnValue = delegate.methodWithMapReturn();
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (Map.Entry<Integer, String> returnValueEntry: returnValue.entrySet()) {
                        if (returnValueEntry.getKey() == null) {
                            reply.writeByte((byte) -1);
                        } else {
                            reply.writeByte((byte) 0);
                            reply.writeInt(returnValueEntry.getKey());
                        }
                        reply.writeString(returnValueEntry.getValue());
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}