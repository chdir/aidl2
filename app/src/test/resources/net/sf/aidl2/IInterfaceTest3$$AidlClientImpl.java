// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import net.sf.fakenames.aidl2.demo.Responder;

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

    public IInterfaceTest3$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Responder methodWithOldAidlCallbackReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IInterfaceTest3$$AidlServerImpl.DESCRIPTOR);

            this.delegate.transact(IInterfaceTest3$$AidlServerImpl.TRANSACT_methodWithOldAidlCallbackReturn, data, reply, 0);
            reply.readException();

            return Responder.Stub.asInterface(reply.readStrongBinder());
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
