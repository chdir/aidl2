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
public final class MethodTypeargTheObject$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.MethodTypeargTheObject";

    static final int TRANSACT_methodWithObjectParameter = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final MethodTypeargTheObject delegate;

    public MethodTypeargTheObject$$AidlServerImpl(MethodTypeargTheObject delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithObjectParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Object object = data.readValue(getClass().getClassLoader());

                delegate.methodWithObjectParameter(AidlUtil.unsafeCast(object));
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
