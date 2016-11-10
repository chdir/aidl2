// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConcreteSetTypeArgsAndTypeLoss2$$AidlClientImpl<H extends TreeSet<String>> implements ConcreteSetTypeArgsAndTypeLoss2<H> {
    private final IBinder delegate;

    public ConcreteSetTypeArgsAndTypeLoss2$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public <J extends ConcurrentSkipListSet<List<Set<Binder>>>> H[] methodWithCSKLSetParamAndTreeSetArray(J concurrentSkipListSet) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteSetTypeArgsAndTypeLoss2$$AidlServerImpl.DESCRIPTOR);

            if (concurrentSkipListSet == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(concurrentSkipListSet.size());
                for (Collection<? extends Collection<? extends IBinder>> concurrentSkipListSetElement : concurrentSkipListSet) {
                    if (concurrentSkipListSetElement == null) {
                        data.writeInt(-1);
                    } else {
                        data.writeInt(concurrentSkipListSetElement.size());
                        for (Collection<? extends IBinder> concurrentSkipListSetElement_ : concurrentSkipListSetElement) {
                            if (concurrentSkipListSetElement_ == null) {
                                data.writeInt(-1);
                            } else {
                                data.writeInt(concurrentSkipListSetElement_.size());
                                for (IBinder concurrentSkipListSetElement__ : concurrentSkipListSetElement_) {
                                    data.writeStrongBinder(concurrentSkipListSetElement__);
                                }
                            }
                        }
                    }
                }
            }

            delegate.transact(ConcreteSetTypeArgsAndTypeLoss2$$AidlServerImpl.TRANSACT_methodWithCSKLSetParamAndTreeSetArray, data, reply, 0);
            reply.readException();

            final TreeSet[] returnValueArray;
            final int returnValueLength = reply.readInt();
            if (returnValueLength < 0) {
                returnValueArray = null;
            } else {
                returnValueArray = new TreeSet[returnValueLength];
                for (int i = 0; i < returnValueArray.length; i++) {
                    final TreeSet<String> returnValueCollection;
                    final int returnValueSize = reply.readInt();
                    if (returnValueSize < 0) {
                        returnValueCollection = null;
                    } else {
                        returnValueCollection = new TreeSet<>();
                        for (int j = 0; j < returnValueSize; j++) {
                            returnValueCollection.add(reply.readString());
                        }
                    }
                    returnValueArray[i] = returnValueCollection;
                }
            }
            return AidlUtil.unsafeCast(returnValueArray);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
