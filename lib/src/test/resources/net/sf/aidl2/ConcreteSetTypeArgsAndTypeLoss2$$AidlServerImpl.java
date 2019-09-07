// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConcreteSetTypeArgsAndTypeLoss2$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ConcreteSetTypeArgsAndTypeLoss2";

    static final int TRANSACT_methodWithCSKLSetParamAndTreeSetArray = IBinder.FIRST_CALL_TRANSACTION;

    private final ConcreteSetTypeArgsAndTypeLoss2<?> delegate;

    public ConcreteSetTypeArgsAndTypeLoss2$$AidlServerImpl(ConcreteSetTypeArgsAndTypeLoss2<?> delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithCSKLSetParamAndTreeSetArray: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final ConcurrentSkipListSet<List<Set<Binder>>> concurrentSkipListSetCollection;
                final int concurrentSkipListSetSize = data.readInt();
                if (concurrentSkipListSetSize < 0) {
                    concurrentSkipListSetCollection = null;
                } else {
                    concurrentSkipListSetCollection = new ConcurrentSkipListSet<>();
                    for (int j = 0; j < concurrentSkipListSetSize; j++) {
                        final ArrayList<Set<Binder>> concurrentSkipListSetCollection_;
                        final int concurrentSkipListSetSize_ = data.readInt();
                        if (concurrentSkipListSetSize_ < 0) {
                            concurrentSkipListSetCollection_ = null;
                        } else {
                            concurrentSkipListSetCollection_ = new ArrayList<>(concurrentSkipListSetSize_);
                            for (int j_ = 0; j_ < concurrentSkipListSetSize_; j_++) {
                                final HashSet<Binder> concurrentSkipListSetCollection__;
                                final int concurrentSkipListSetSize__ = data.readInt();
                                if (concurrentSkipListSetSize__ < 0) {
                                    concurrentSkipListSetCollection__ = null;
                                } else {
                                    concurrentSkipListSetCollection__ = new HashSet<>(concurrentSkipListSetSize__);
                                    for (int j__ = 0; j__ < concurrentSkipListSetSize__; j__++) {
                                        concurrentSkipListSetCollection__.add((Binder) data.readStrongBinder());
                                    }
                                }
                                concurrentSkipListSetCollection_.add(concurrentSkipListSetCollection__);
                            }
                        }
                        concurrentSkipListSetCollection.add(concurrentSkipListSetCollection_);
                    }
                }

                final TreeSet<String>[] returnValue = delegate.methodWithCSKLSetParamAndTreeSetArray(AidlUtil.unsafeCast(concurrentSkipListSetCollection));
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.length);
                    for (TreeSet<String> returnValueComponent : returnValue) {
                        if (returnValueComponent == null) {
                            reply.writeInt(-1);
                        } else {
                            reply.writeInt(returnValueComponent.size());
                            for (String returnValueComponentElement : returnValueComponent) {
                                reply.writeString(returnValueComponentElement);
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
