// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.SizeF;
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
public final class NotNullSizeArrayParameter$$AidlClientImpl implements NotNullSizeArrayParameter {
    private final IBinder delegate;

    public NotNullSizeArrayParameter$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithNotNullSizeArrayParameter(@NotNull SizeF[] sizeArrayParam) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(NotNullSizeArrayParameter$$AidlServerImpl.DESCRIPTOR);

            if (sizeArrayParam == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(sizeArrayParam.length);
                for (SizeF sizeArrayParamComponent : sizeArrayParam) {
                    data.writeSizeF(sizeArrayParamComponent);
                }
            }

            delegate.transact(NotNullSizeArrayParameter$$AidlServerImpl.TRANSACT_methodWithNotNullSizeArrayParameter, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
