// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.SizeF;
import java.lang.Deprecated;
import java.lang.Override;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class AbstractListTest$$AidlClientImpl implements AbstractListTest {
    private final IBinder delegate;

    public AbstractListTest$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public Collection<AbstractListTest> methodWithCollectionReturn(AbstractList<SizeF> abstrList) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(AbstractListTest$$AidlServerImpl.DESCRIPTOR);

            if (abstrList == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(abstrList.size());
                for (SizeF abstrListElement : abstrList) {
                    if (abstrListElement == null) {
                        data.writeByte((byte) -1);
                    } else {
                        data.writeByte((byte) 0);
                        data.writeSizeF(abstrListElement);
                    }
                }
            }

            delegate.transact(AbstractListTest$$AidlServerImpl.TRANSACT_methodWithCollectionReturn, data, reply, 0);
            reply.readException();

            final ArrayList<AbstractListTest> returnValueCollection;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueCollection = null;
            } else {
                returnValueCollection = new ArrayList<>(returnValueSize);
                for (int j = 0; j < returnValueSize; j++) {
                    final IBinder returnValueBinder = reply.readStrongBinder();
                    final AbstractListTest iReturnValue = returnValueBinder == null ? null : InterfaceLoader.asInterface(returnValueBinder, AbstractListTest.class);
                    returnValueCollection.add(iReturnValue);
                }
            }
            return returnValueCollection;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
