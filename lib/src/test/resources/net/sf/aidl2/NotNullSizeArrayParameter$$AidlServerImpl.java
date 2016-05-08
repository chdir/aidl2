// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.SizeF;
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
public final class NotNullSizeArrayParameter$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.NotNullSizeArrayParameter";

    static final int TRANSACT_methodWithNotNullSizeArrayParameter = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final NotNullSizeArrayParameter delegate;

    public NotNullSizeArrayParameter$$AidlServerImpl(NotNullSizeArrayParameter delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithNotNullSizeArrayParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final SizeF[] sizeArrayParamArray;
                final int sizeArrayParamLength = data.readInt();
                if (sizeArrayParamLength < 0) {
                    sizeArrayParamArray = null;
                } else {
                    sizeArrayParamArray = new SizeF[sizeArrayParamLength];
                    for (int i = 0; i < sizeArrayParamArray.length; i++) {
                        sizeArrayParamArray[i] = data.readSizeF();
                    }
                }

                delegate.methodWithNotNullSizeArrayParameter(sizeArrayParamArray);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
