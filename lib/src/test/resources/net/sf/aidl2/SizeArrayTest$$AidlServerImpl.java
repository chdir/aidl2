// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Size;
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
public final class SizeArrayTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.SizeArrayTest";

    static final int TRANSACT_methodWithSizeReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final SizeArrayTest delegate;

    public SizeArrayTest$$AidlServerImpl(SizeArrayTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithSizeReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                Size[] returnValue = this.delegate.methodWithSizeReturn();
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.length);
                    for (Size returnValueComponent : returnValue) {
                        if (returnValueComponent == null) {
                            reply.writeByte((byte) -1);
                        } else {
                            reply.writeByte((byte) 0);
                            reply.writeSize(returnValueComponent);
                        }
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
