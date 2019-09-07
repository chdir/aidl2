// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import org.jetbrains.annotations.NotNull;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IInterfaceTest3$$AidlClientImpl implements IInterfaceTest3 {
    private final IBinder delegate;

    public IInterfaceTest3$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    @NotNull
    public RemoteApi methodWithCallbackReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IInterfaceTest3$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(IInterfaceTest3$$AidlServerImpl.TRANSACT_methodWithCallbackReturn, data, reply, 0);
            reply.readException();

            return InterfaceLoader.asInterface(reply.readStrongBinder(), RemoteApi.class);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
