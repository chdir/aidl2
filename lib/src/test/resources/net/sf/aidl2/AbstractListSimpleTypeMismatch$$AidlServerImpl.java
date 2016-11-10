// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
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
public final class AbstractListSimpleTypeMismatch$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.AbstractListSimpleTypeMismatch";

    static final int TRANSACT_methodWithListReturnAndCollectionParam = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final AbstractListSimpleTypeMismatch delegate;

    public AbstractListSimpleTypeMismatch$$AidlServerImpl(AbstractListSimpleTypeMismatch delegate) {
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

                final Collection<? extends IBinder> returnValue = delegate.methodWithListReturnAndCollectionParam(intsCollection);
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
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
