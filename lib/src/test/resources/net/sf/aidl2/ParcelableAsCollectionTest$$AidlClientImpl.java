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
public final class ParcelableAsCollectionTest$$AidlClientImpl implements ParcelableAsCollectionTest {
    private final IBinder delegate;

    public ParcelableAsCollectionTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public @As(DataKind.SEQUENCE) ParcelableCollection methodWithCollectionReturn() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ParcelableAsCollectionTest$$AidlServerImpl.DESCRIPTOR);

            delegate.transact(ParcelableAsCollectionTest$$AidlServerImpl.TRANSACT_methodWithCollectionReturn, data, reply, 0);
            reply.readException();

            final ParcelableCollection returnValueCollection;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueCollection = null;
            } else {
                returnValueCollection = new ParcelableCollection();
                for (int j = 0; j < returnValueSize; j++) {
                    final Integer returnValueTmp;
                    if (reply.readByte() == -1) {
                        returnValueTmp = null;
                    } else {
                        returnValueTmp = reply.readInt();
                    }
                    returnValueCollection.add(returnValueTmp);
                }
            }
            return returnValueCollection;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}