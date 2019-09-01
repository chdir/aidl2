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
import java.util.Date;
import java.util.List;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class AbstractListSimpleNested$$AidlClientImpl implements AbstractListSimpleNested {
    private final IBinder delegate;

    public AbstractListSimpleNested$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public AbstractList<List<Date>> methodWithAbstractListReturn(List<SizeF[][]> sizes) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(AbstractListSimpleNested$$AidlServerImpl.DESCRIPTOR);

            if (sizes == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(sizes.size());
                for (SizeF[][] sizesElement : sizes) {
                    if (sizesElement == null) {
                        data.writeInt(-1);
                    } else {
                        data.writeInt(sizesElement.length);
                        for (SizeF[] sizesElementComponent : sizesElement) {
                            if (sizesElementComponent == null) {
                                data.writeInt(-1);
                            } else {
                                data.writeInt(sizesElementComponent.length);
                                for (SizeF sizesElementComponent_ : sizesElementComponent) {
                                    if (sizesElementComponent_ == null) {
                                        data.writeByte((byte) -1);
                                    } else {
                                        data.writeByte((byte) 0);
                                        data.writeSizeF(sizesElementComponent_);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            delegate.transact(AbstractListSimpleNested$$AidlServerImpl.TRANSACT_methodWithAbstractListReturn, data, reply, 0);
            reply.readException();

            final ArrayList<List<Date>> returnValueCollection;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueCollection = null;
            } else {
                returnValueCollection = new ArrayList<>(returnValueSize);
                for (int j = 0; j < returnValueSize; j++) {
                    final ArrayList<Date> returnValueCollection_;
                    final int returnValueSize_ = reply.readInt();
                    if (returnValueSize_ < 0) {
                        returnValueCollection_ = null;
                    } else {
                        returnValueCollection_ = new ArrayList<>(returnValueSize_);
                        for (int j_ = 0; j_ < returnValueSize_; j_++) {
                            returnValueCollection_.add(AidlUtil.readFromObjectStream(reply));
                        }
                    }
                    returnValueCollection.add(returnValueCollection_);
                }
            }
            return returnValueCollection;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}