// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Size;
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
public final class SizeArrayTest$$AidlClientImpl implements SizeArrayTest {
    private final IBinder delegate;

    public SizeArrayTest$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Size[] methodWithSizeReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(SizeArrayTest$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(SizeArrayTest$$AidlServerImpl.TRANSACT_methodWithSizeReturn, data, reply, 0);
            reply.readException();

            final Size[] returnValueArray;
            final int returnValueLength = reply.readInt();
            if (returnValueLength < 0) {
                returnValueArray = null;
            } else {
                returnValueArray = new Size[returnValueLength];
                for (int i = 0; i < returnValueArray.length; i++) {
                    final Size returnValueTmp;
                    if (reply.readByte() == -1) {
                        returnValueTmp = null;
                    } else {
                        returnValueTmp = reply.readSize();
                    }
                    returnValueArray[i] = returnValueTmp;
                }
            }
            return returnValueArray;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
