// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
@SuppressWarnings("unchecked")
public final class ConcreteListClassTypeParamAndRaw$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ConcreteListClassTypeParamAndRaw";

    static final int TRANSACT_methodWithLinkedListParamAndArrayListReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final ConcreteListClassTypeParamAndRaw delegate;

    public ConcreteListClassTypeParamAndRaw$$AidlServerImpl(ConcreteListClassTypeParamAndRaw delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithLinkedListParamAndArrayListReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final LinkedList<Object> objectListCollection;
                final int objectListSize = data.readInt();
                if (objectListSize < 0) {
                    objectListCollection = null;
                } else {
                    objectListCollection = new LinkedList<>();
                    for (int j = 0; j < objectListSize; j++) {
                        objectListCollection.add(data.readValue(getClass().getClassLoader()));
                    }
                }

                final Collection<?> returnValue = delegate.methodWithLinkedListParamAndArrayListReturn(AidlUtil.unsafeCast(objectListCollection));
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (Object returnValueElement : returnValue) {
                        reply.writeValue(returnValueElement);
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
