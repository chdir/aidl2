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
public final class ClassTypeargParcelable$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.MethodTypeargParcelable";

    static final int TRANSACT_methodWithParcelableReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final ClassTypeargParcelable delegate;

    public ClassTypeargParcelable$$AidlServerImpl(ClassTypeargParcelable delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithParcelableReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                Parcelable returnValue = (Parcelable) this.delegate.methodWithParcelableReturn();
                reply.writeNoException();
                reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}