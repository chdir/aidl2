// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Long;
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
public final class LongTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.LongTest";

    static final int TRANSACT_methodWithLongParameter = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final LongTest delegate;

    public LongTest$$AidlServerImpl(LongTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithLongParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Long longParameterTmp;
                if (data.readByte() == -1) {
                    longParameterTmp = null;
                } else {
                    longParameterTmp = data.readLong();
                }

                this.delegate.methodWithLongParameter(longParameterTmp);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
