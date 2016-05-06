// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
@SuppressWarnings("unchecked")
public final class ConcreteListClassTypeParamAndRaw$$AidlClientImpl<X extends LinkedList<Object>> implements ConcreteListClassTypeParamAndRaw<X> {
    private final IBinder delegate;

    public ConcreteListClassTypeParamAndRaw$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public ArrayList<? super String> methodWithLinkedListParamAndArrayListReturn(X objectList) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteListClassTypeParamAndRaw$$AidlServerImpl.DESCRIPTOR);

            if (objectList == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(objectList.size());
                for (Object objectListElement : objectList) {
                    data.writeValue(objectListElement);
                }
            }

            delegate.transact(ConcreteListClassTypeParamAndRaw$$AidlServerImpl.TRANSACT_methodWithLinkedListParamAndArrayListReturn, data, reply, 0);
            reply.readException();

            final ArrayList<Object> returnValueCollection;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueCollection = null;
            } else {
                returnValueCollection = new ArrayList<>(returnValueSize);
                for (int j = 0; j < returnValueSize; j++) {
                    returnValueCollection.add(reply.readValue(getClass().getClassLoader()));
                }
            }
            return returnValueCollection;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
