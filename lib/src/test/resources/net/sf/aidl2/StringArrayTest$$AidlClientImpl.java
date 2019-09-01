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
public final class StringArrayTest$$AidlClientImpl implements StringArrayTest {
    private final IBinder delegate;

    public StringArrayTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public String[] methodWithStringReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(StringArrayTest$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(StringArrayTest$$AidlServerImpl.TRANSACT_methodWithStringReturn, data, reply, 0);
            reply.readException();

            final String[] returnValueArray;
            final int returnValueLength = reply.readInt();
            if (returnValueLength < 0) {
                returnValueArray = null;
            } else {
                returnValueArray = new String[returnValueLength];
                for (int i = 0; i < returnValueArray.length; i++) {
                    returnValueArray[i] = reply.readString();
                }
            }
            return returnValueArray;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
