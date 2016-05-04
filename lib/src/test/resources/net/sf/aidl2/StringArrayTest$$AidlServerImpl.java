// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
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
public final class StringArrayTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.StringArrayTest";

    static final int TRANSACT_methodWithStringReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final StringArrayTest delegate;

    public StringArrayTest$$AidlServerImpl(StringArrayTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithStringReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final String[] returnValue = delegate.methodWithStringReturn();
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.length);
                    for (String returnValueComponent : returnValue) {
                        reply.writeString(returnValueComponent);
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}