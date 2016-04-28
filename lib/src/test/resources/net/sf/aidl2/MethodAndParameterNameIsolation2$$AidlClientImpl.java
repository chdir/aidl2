// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MethodAndParamNameIsolation2$$AidlClientImpl implements MethodAndParamNameIsolation2 {
    private final IBinder delegate;

    public MethodAndParamNameIsolation2$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void bigDeal(String theStringVariable) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MethodAndParamNameIsolation2$$AidlServerImpl.DESCRIPTOR);

            data.writeString(theStringVariable);

            this.delegate.transact(MethodAndParamNameIsolation2$$AidlServerImpl.TRANSACT_bigDeal, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public void bigDeal(String theStringVariable, String anotherString) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MethodAndParamNameIsolation2$$AidlServerImpl.DESCRIPTOR);

            data.writeString(theStringVariable);

            data.writeString(anotherString);

            this.delegate.transact(MethodAndParamNameIsolation2$$AidlServerImpl.TRANSACT_bigDeal, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public void bigDeal(String theStringVariable, String anotherString, cunningType cunningType) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MethodAndParamNameIsolation2$$AidlServerImpl.DESCRIPTOR);

            data.writeString(theStringVariable);

            data.writeString(anotherString);

            data.writeSerializable(cunningType);

            this.delegate.transact(MethodAndParamNameIsolation2$$AidlServerImpl.TRANSACT_bigDeal, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public void cunningType(String theStringVariable, String anotherString, cunningType cunningType, MethodAndParamNameIsolation2 MethodAndParamNameIsolation2) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MethodAndParamNameIsolation2$$AidlServerImpl.DESCRIPTOR);

            data.writeString(theStringVariable);

            data.writeString(anotherString);

            data.writeSerializable(cunningType);

            data.writeStrongBinder(MethodAndParamNameIsolation2 == null ? null : MethodAndParamNameIsolation2.asBinder());

            this.delegate.transact(MethodAndParamNameIsolation2$$AidlServerImpl.TRANSACT_cunningType, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public void cunningType(String theStringVariable, String anotherString, cunningType cunningType, MethodAndParamNameIsolation2 MethodAndParamNameIsolation2, RemoteException... RemoteException) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MethodAndParamNameIsolation2$$AidlServerImpl.DESCRIPTOR);

            data.writeString(theStringVariable);

            data.writeString(anotherString);

            data.writeSerializable(cunningType);

            data.writeStrongBinder(MethodAndParamNameIsolation2 == null ? null : MethodAndParamNameIsolation2.asBinder());

            data.writeSerializable(RemoteException);

            this.delegate.transact(MethodAndParamNameIsolation2$$AidlServerImpl.TRANSACT_cunningType, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
