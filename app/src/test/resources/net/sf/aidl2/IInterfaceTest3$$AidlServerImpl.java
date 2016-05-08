// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import net.sf.fakenames.aidl2.demo.Responder;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IInterfaceTest3$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.IInterfaceTest3";

    static final int TRANSACT_methodWithOldAidlCallbackReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final IInterfaceTest3 delegate;

    public IInterfaceTest3$$AidlServerImpl(IInterfaceTest3 delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithOldAidlCallbackReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Responder returnValue = delegate.methodWithOldAidlCallbackReturn();
                reply.writeNoException();

                reply.writeStrongBinder(returnValue == null ? null : returnValue.asBinder());

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
