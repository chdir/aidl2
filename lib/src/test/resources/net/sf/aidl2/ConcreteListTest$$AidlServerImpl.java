// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import java.lang.CharSequence;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConcreteListTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ConcreteListTest";

    static final int TRANSACT_methodWithLinkedListParamAndArrayListReturn = IBinder.FIRST_CALL_TRANSACTION + 0;

    static final int TRANSACT_methodWithVectorParamAndStackReturn = IBinder.FIRST_CALL_TRANSACTION + 1;

    private final ConcreteListTest delegate;

    public ConcreteListTest$$AidlServerImpl(ConcreteListTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    @SuppressWarnings("unchecked")
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

                final ArrayList<? super String> returnValue = delegate.methodWithLinkedListParamAndArrayListReturn(AidlUtil.unsafeCast(objectListCollection));
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
            } case TRANSACT_methodWithVectorParamAndStackReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Vector<Parcelable> objectListCollection;
                final int objectListSize = data.readInt();
                if (objectListSize < 0) {
                    objectListCollection = null;
                } else {
                    objectListCollection = new Vector<>(objectListSize);
                    for (int j = 0; j < objectListSize; j++) {
                        objectListCollection.add(data.readParcelable(getClass().getClassLoader()));
                    }
                }

                Stack<? extends CharSequence> returnValue = this.delegate.methodWithVectorParamAndStackReturn(objectListCollection);
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (CharSequence returnValueElement : returnValue) {
                        TextUtils.writeToParcel(returnValueElement, reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
