// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.SizeF;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class AbstractListTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.AbstractListTest";

    static final int TRANSACT_methodWithListReturnAndCollectionParam = IBinder.FIRST_CALL_TRANSACTION + 0;

    static final int TRANSACT_methodWithCollectionReturn = IBinder.FIRST_CALL_TRANSACTION + 1;

    static final int TRANSACT_methodWithAbstractListReturn = IBinder.FIRST_CALL_TRANSACTION + 2;

    private final AbstractListTest delegate;

    public AbstractListTest$$AidlServerImpl(AbstractListTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithListReturnAndCollectionParam: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final ArrayList<Integer> intsCollection;
                final int intsSize = data.readInt();
                if (intsSize < 0) {
                    intsCollection = null;
                } else {
                    intsCollection = new ArrayList<>(intsSize);
                    for (int j = 0; j < intsSize; j++) {
                        final Integer intsTmp;
                        if (data.readByte() == -1) {
                            intsTmp = null;
                        } else {
                            intsTmp = data.readInt();
                        }
                        intsCollection.add(intsTmp);
                    }
                }

                final List<Binder> returnValue = delegate.methodWithListReturnAndCollectionParam(intsCollection);
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (IBinder returnValueElement : returnValue) {
                        reply.writeStrongBinder(returnValueElement);
                    }
                }

                return true;
            } case TRANSACT_methodWithCollectionReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final ArrayList<SizeF> abstrListCollection;
                final int abstrListSize = data.readInt();
                if (abstrListSize < 0) {
                    abstrListCollection = null;
                } else {
                    abstrListCollection = new ArrayList<>(abstrListSize);
                    for (int j = 0; j < abstrListSize; j++) {
                        final SizeF abstrListTmp;
                        if (data.readByte() == -1) {
                            abstrListTmp = null;
                        } else {
                            abstrListTmp = data.readSizeF();
                        }
                        abstrListCollection.add(abstrListTmp);
                    }
                }

                final Collection<AbstractListTest> returnValue = delegate.methodWithCollectionReturn(abstrListCollection);
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (AbstractListTest returnValueElement : returnValue) {
                        reply.writeStrongBinder(returnValueElement == null ? null : returnValueElement.asBinder());
                    }
                }

                return true;
            } case TRANSACT_methodWithAbstractListReturn: {
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

                final AbstractList<List<Date>> returnValue = delegate.methodWithAbstractListReturn(sizesCollection);
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (List<Date> returnValueElement : returnValue) {
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
