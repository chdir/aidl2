// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import java.lang.CharSequence;
import java.lang.Deprecated;
import java.lang.Override;
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
public final class ConcreteListMethodTypeParam$$AidlClientImpl implements ConcreteListMethodTypeParam {
    private final IBinder delegate;

    public ConcreteListMethodTypeParam$$AidlClientImpl(IBinder delegate) {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public <T extends CharSequence> Stack<T> methodWithVectorParamAndStackReturn(Vector<Parcelable> objectList) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ConcreteListMethodTypeParam$$AidlServerImpl.DESCRIPTOR);

            if (objectList == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(objectList.size());
                for (Parcelable objectListElement : objectList) {
                    data.writeParcelable(objectListElement, 0);
                }
            }

            delegate.transact(ConcreteListMethodTypeParam$$AidlServerImpl.TRANSACT_methodWithVectorParamAndStackReturn, data, reply, 0);
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
            return (Stack) returnValueCollection;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
