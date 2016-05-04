// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

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
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConcreteListTest$$AidlClientImpl<X extends LinkedList<Object>> implements ConcreteListTest<X> {
    private final IBinder delegate;

    public ConcreteListTest$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<? super String> methodWithLinkedListParamAndArrayListReturn(X objectList) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteListTest$$AidlServerImpl.DESCRIPTOR);

            if (objectList == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(objectList.size());
                for (Object objectListElement : objectList) {
                    data.writeValue(objectListElement);
                }
            }

            delegate.transact(ConcreteListTest$$AidlServerImpl.TRANSACT_methodWithLinkedListParamAndArrayListReturn, data, reply, 0);
            reply.readException();

            final ArrayList<Object> returnValueCollection;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueCollection = null;
            } else {
                returnValueCollection = new ArrayList<>(returnValueSize);
                for (int j = 0; j < returnValueSize; j++) {
                    returnValueCollection.add(AidlUtil.unsafeCast(reply.readValue(getClass().getClassLoader())));
                }
            }
            return returnValueCollection;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override
    public <T extends CharSequence> Stack<T> methodWithVectorParamAndStackReturn(Vector<Parcelable> objectList) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteListTest$$AidlServerImpl.DESCRIPTOR);

            if (objectList == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(objectList.size());
                for (Parcelable objectListElement : objectList) {
                    data.writeParcelable(objectListElement, 0);
                }
            }

            delegate.transact(ConcreteListTest$$AidlServerImpl.TRANSACT_methodWithVectorParamAndStackReturn, data, reply, 0);
            reply.readException();

            final Stack<CharSequence> returnValueCollection;
            final int returnValueSize = reply.readInt();
            if (returnValueSize < 0) {
                returnValueCollection = null;
            } else {
                returnValueCollection = new Stack<>();
                for (int j = 0; j < returnValueSize; j++) {
                    returnValueCollection.add(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(reply));
                }
            }
            return AidlUtil.unsafeCast(returnValueCollection);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
