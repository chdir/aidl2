// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IInterfaceTest2$$AidlClientImpl implements IInterfaceTest2 {
    private final IBinder delegate;

    public IInterfaceTest2$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public RemoteApi methodWithCallbackReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IInterfaceTest2$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(IInterfaceTest2$$AidlServerImpl.TRANSACT_methodWithCallbackReturn, data, reply, 0);
            reply.readException();

            final IBinder returnValueBinder = reply.readStrongBinder();
            final RemoteApi iReturnValue = returnValueBinder == null ? null : InterfaceLoader.asInterface(returnValueBinder, RemoteApi.class);
            return iReturnValue;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
