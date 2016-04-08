// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class TheObjectReturn$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.TheObjectReturn";

    static final int TRANSACT_methodWithObjectReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final TheObjectReturn delegate;

    public TheObjectReturn$$AidlServerImpl(TheObjectReturn delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithObjectReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                Object returnValue = this.delegate.methodWithObjectReturn();
                reply.writeNoException();

                reply.writeValue(returnValue);

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
