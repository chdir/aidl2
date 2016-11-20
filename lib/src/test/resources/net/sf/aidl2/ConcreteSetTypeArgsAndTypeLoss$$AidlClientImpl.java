// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Override;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConcreteSetTypeArgsAndTypeLoss$$AidlClientImpl<X extends Callable & Externalizable> implements ConcreteSetTypeArgsAndTypeLoss<X> {
    private final IBinder delegate;

    public ConcreteSetTypeArgsAndTypeLoss$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public <Y extends Serializable & Parcelable> HashSet<? extends X> methodWithCOWArraySetParamAndHashSetReturn(CopyOnWriteArraySet<Y> objectList) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteSetTypeArgsAndTypeLoss$$AidlServerImpl.DESCRIPTOR);

            if (objectList == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(objectList.size());
                for (Parcelable objectListElement : objectList) {
                    data.writeParcelable(objectListElement, 0);
                }
            }

            delegate.transact(ConcreteSetTypeArgsAndTypeLoss$$AidlServerImpl.TRANSACT_methodWithCOWArraySetParamAndHashSetReturn, data, reply, 0);
            reply.readException();

            return AidlUtil.readFromObjectStream(reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
