// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.Bundle;
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
public final class BundleTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.BundleTest";

    static final int TRANSACT_methodWithBundleParameter = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final BundleTest delegate;

    public BundleTest$$AidlServerImpl(BundleTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithBundleParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Bundle bundle = data.readBundle(getClass().getClassLoader());

                Bundle returnValue = this.delegate.methodWithBundleParameter(bundle);
                reply.writeNoException();

                reply.writeBundle(returnValue);

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}