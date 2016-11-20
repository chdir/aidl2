// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConcreteSetTypeArgsAndTypeLoss$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ConcreteSetTypeArgsAndTypeLoss";

    static final int TRANSACT_methodWithCOWArraySetParamAndHashSetReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final ConcreteSetTypeArgsAndTypeLoss delegate;

    public ConcreteSetTypeArgsAndTypeLoss$$AidlServerImpl(ConcreteSetTypeArgsAndTypeLoss delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithCOWArraySetParamAndHashSetReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final CopyOnWriteArraySet<Parcelable> objectListCollection;
                final int objectListSize = data.readInt();
                if (objectListSize < 0) {
                    objectListCollection = null;
                } else {
                    objectListCollection = new CopyOnWriteArraySet<>();
                    for (int j = 0; j < objectListSize; j++) {
                        objectListCollection.add(data.readParcelable(getClass().getClassLoader()));
                    }
                }

                final Serializable returnValue = delegate.methodWithCOWArraySetParamAndHashSetReturn((CopyOnWriteArraySet) objectListCollection);
                reply.writeNoException();

                AidlUtil.writeToObjectStream(reply, returnValue);

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
