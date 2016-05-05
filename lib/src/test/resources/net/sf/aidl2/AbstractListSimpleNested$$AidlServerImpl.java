// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.SizeF;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class AbstractListSimpleNested$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.AbstractListSimpleNested";

    static final int TRANSACT_methodWithAbstractListReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final AbstractListSimpleNested delegate;

    public AbstractListSimpleNested$$AidlServerImpl(AbstractListSimpleNested delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithAbstractListReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final ArrayList<SizeF[][]> sizesCollection;
                final int sizesSize = data.readInt();
                if (sizesSize < 0) {
                    sizesCollection = null;
                } else {
                    sizesCollection = new ArrayList<>(sizesSize);
                    for (int j = 0; j < sizesSize; j++) {
                        final SizeF[][] sizesArray;
                        final int sizesLength = data.readInt();
                        if (sizesLength < 0) {
                            sizesArray = null;
                        } else {
                            sizesArray = new SizeF[sizesLength][];
                            for (int i = 0; i < sizesArray.length; i++) {
                                final SizeF[] sizesArray_;
                                final int sizesLength_ = data.readInt();
                                if (sizesLength_ < 0) {
                                    sizesArray_ = null;
                                } else {
                                    sizesArray_ = new SizeF[sizesLength_];
                                    for (int i_ = 0; i_ < sizesArray_.length; i_++) {
                                        final SizeF sizesTmp;
                                        if (data.readByte() == -1) {
                                            sizesTmp = null;
                                        } else {
                                            sizesTmp = data.readSizeF();
                                        }
                                        sizesArray_[i_] = sizesTmp;
                                    }
                                }
                                sizesArray[i] = sizesArray_;
                            }
                        }
                        sizesCollection.add(sizesArray);
                    }
                }

                final Collection<Collection<Serializable>> returnValue = (Collection) delegate.methodWithAbstractListReturn(sizesCollection);
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (Collection<Serializable> returnValueElement : returnValue) {
                        if (returnValueElement == null) {
                            reply.writeInt(-1);
                        } else {
                            reply.writeInt(returnValueElement.size());
                            for (Serializable returnValueElement_ : returnValueElement) {
                                reply.writeSerializable(returnValueElement_);
                            }
                        }
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
