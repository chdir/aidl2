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
public final class BooleanArrayTest2$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.BooleanArrayTest2";

    static final int TRANSACT_methodWithBooleanArrayReturn = IBinder.FIRST_CALL_TRANSACTION;

    private final BooleanArrayTest2 delegate;

    public BooleanArrayTest2$$AidlServerImpl(BooleanArrayTest2 delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithBooleanArrayReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final boolean[] returnValue = delegate.methodWithBooleanArrayReturn();
                reply.writeNoException();

                reply.writeBooleanArray(returnValue);

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}