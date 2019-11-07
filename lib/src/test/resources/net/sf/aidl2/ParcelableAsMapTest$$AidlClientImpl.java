// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ParcelableAsMapTest$$AidlClientImpl implements ParcelableAsMapTest {
    private final IBinder delegate;

    public ParcelableAsMapTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public @As(DataKind.MAP) ParcelableMap methodWithMapReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ParcelableAsMapTest$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ParcelableAsMapTest$$AidlServerImpl.TRANSACT_methodWithMapReturn, data, reply, 0);
            reply.readException();

            final ParcelableMap returnValueMap;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueMap = null;
            } else {
                returnValueMap = new ParcelableMap();
                for (int k = 0; k < returnValueSize; k++) {
                    final Integer returnValueTmp;
                    if (reply.readByte() == -1) {
                        returnValueTmp = null;
                    } else {
                        returnValueTmp = reply.readInt();
                    }
                    returnValueMap.put(returnValueTmp, reply.readString());
                }
            }
            return returnValueMap;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}