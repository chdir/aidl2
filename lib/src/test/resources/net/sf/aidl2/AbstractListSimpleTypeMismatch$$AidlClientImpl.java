// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class AbstractListSimpleTypeMismatch$$AidlClientImpl implements AbstractListSimpleTypeMismatch {
    private final IBinder delegate;

    public AbstractListSimpleTypeMismatch$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public List<Binder> methodWithListReturnAndCollectionParam(Collection<Integer> ints) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(AbstractListSimpleTypeMismatch$$AidlServerImpl.DESCRIPTOR);

            if (ints == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(ints.size());
                for (Integer intsElement : ints) {
                    if (intsElement == null) {
                        data.writeByte((byte) -1);
                    } else {
                        data.writeByte((byte) 0);
                        data.writeInt(intsElement);
                    }
                }
            }

            delegate.transact(AbstractListSimpleTypeMismatch$$AidlServerImpl.TRANSACT_methodWithListReturnAndCollectionParam, data, reply, 0);
            reply.readException();

            final ArrayList<Binder> returnValueCollection;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueCollection = null;
            } else {
                returnValueCollection = new ArrayList<>(returnValueSize);
                for (int j = 0; j < returnValueSize; j++) {
                    returnValueCollection.add((Binder) reply.readStrongBinder());
                }
            }
            return returnValueCollection;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
