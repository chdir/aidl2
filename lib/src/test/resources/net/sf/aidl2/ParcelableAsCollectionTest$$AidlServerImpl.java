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
import java.util.Collection;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ParcelableAsCollectionTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ParcelableAsCollectionTest";

    static final int TRANSACT_methodWithCollectionReturn = IBinder.FIRST_CALL_TRANSACTION;

    private final ParcelableAsCollectionTest delegate;

    public ParcelableAsCollectionTest$$AidlServerImpl(ParcelableAsCollectionTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithCollectionReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Collection<Integer> returnValue = delegate.methodWithCollectionReturn();
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (Integer returnValueElement : returnValue) {
                        if (returnValueElement == null) {
                            reply.writeByte((byte) -1);
                        } else {
                            reply.writeByte((byte) 0);
                            reply.writeInt(returnValueElement);
                        }
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}