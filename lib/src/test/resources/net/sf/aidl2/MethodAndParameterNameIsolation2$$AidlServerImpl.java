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
public final class MethodAndParamNameIsolation2$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.MethodAndParamNameIsolation2";

    static final int TRANSACT_bigDeal = IBinder.FIRST_CALL_TRANSACTION + 0;

    static final int TRANSACT_bigDeal_ = IBinder.FIRST_CALL_TRANSACTION + 1;

    static final int TRANSACT_bigDeal__ = IBinder.FIRST_CALL_TRANSACTION + 2;

    static final int TRANSACT_cunningType = IBinder.FIRST_CALL_TRANSACTION + 3;

    static final int TRANSACT_cunningType_ = IBinder.FIRST_CALL_TRANSACTION + 4;

    private final MethodAndParamNameIsolation2 delegate;

    public MethodAndParamNameIsolation2$$AidlServerImpl(MethodAndParamNameIsolation2 delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_bigDeal: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final String theStringVariable = data.readString();

                delegate.bigDeal(theStringVariable);
                reply.writeNoException();

                return true;
            } case TRANSACT_bigDeal_: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final String theStringVariable = data.readString();

                final String anotherString = data.readString();

                delegate.bigDeal(theStringVariable, anotherString);
                reply.writeNoException();

                return true;
            } case TRANSACT_bigDeal__: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final String theStringVariable = data.readString();

                final String anotherString = data.readString();

                final cunningType cunningType = AidlUtil.readSafeSerializable(data);

                delegate.bigDeal(theStringVariable, anotherString, cunningType);
                reply.writeNoException();

                return true;
            } case TRANSACT_cunningType: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final String theStringVariable = data.readString();

                final String anotherString = data.readString();

                final cunningType cunningType = AidlUtil.readSafeSerializable(data);

                final IBinder MethodAndParamNameIsolation2Binder = data.readStrongBinder();
                final MethodAndParamNameIsolation2 iMethodAndParamNameIsolation2 = MethodAndParamNameIsolation2Binder == null ? null : InterfaceLoader.asInterface(MethodAndParamNameIsolation2Binder, MethodAndParamNameIsolation2.class);

                delegate.cunningType(theStringVariable, anotherString, cunningType, iMethodAndParamNameIsolation2);
                reply.writeNoException();

                return true;
            } case TRANSACT_cunningType_: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final String theStringVariable = data.readString();

                final String anotherString = data.readString();

                final cunningType cunningType = AidlUtil.readSafeSerializable(data);

                final IBinder MethodAndParamNameIsolation2Binder = data.readStrongBinder();
                final MethodAndParamNameIsolation2 iMethodAndParamNameIsolation2 = MethodAndParamNameIsolation2Binder == null ? null : InterfaceLoader.asInterface(MethodAndParamNameIsolation2Binder, MethodAndParamNameIsolation2.class);

                final RemoteException[] RemoteException = AidlUtil.readSafeSerializable(data);

                delegate.cunningType(theStringVariable, anotherString, cunningType, iMethodAndParamNameIsolation2, RemoteException);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
