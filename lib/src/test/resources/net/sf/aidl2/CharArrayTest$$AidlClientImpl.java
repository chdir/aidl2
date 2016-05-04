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
public final class CharArrayTest$$AidlClientImpl implements CharArrayTest {
    private final IBinder delegate;

    public CharArrayTest$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public char[][] methodWithBiCharArrayReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(CharArrayTest$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(CharArrayTest$$AidlServerImpl.TRANSACT_methodWithBiCharArrayReturn, data, reply, 0);
            reply.readException();

            final char[][] returnValueArray;
            final int returnValueLength = reply.readInt();
            if (returnValueLength < 0) {
                returnValueArray = null;
            } else {
                returnValueArray = new char[returnValueLength][];
                for (int i = 0; i < returnValueArray.length; i++) {
                    returnValueArray[i] = reply.createCharArray();
                }
            }
            return returnValueArray;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
