// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
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
public final class AbstractParcelableParameter$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.AbstractParcelableParameter";

    static final int TRANSACT_methodWithParcelableParameter = IBinder.FIRST_CALL_TRANSACTION;

    private final AbstractParcelableParameter delegate;

    public AbstractParcelableParameter$$AidlServerImpl(AbstractParcelableParameter delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithParcelableParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Parcelable parcelableParameter = data.readParcelable(getClass().getClassLoader());

                delegate.methodWithParcelableParameter(parcelableParameter);
                reply.writeNoException();
                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
