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
import java.lang.Override;
import java.lang.String;
import java.util.Collection;
import java.util.Vector;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConcreteListMethodTypeParam$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ConcreteListMethodTypeParam";

    static final int TRANSACT_methodWithVectorParamAndStackReturn = IBinder.FIRST_CALL_TRANSACTION;

    private final ConcreteListMethodTypeParam delegate;

    public ConcreteListMethodTypeParam$$AidlServerImpl(ConcreteListMethodTypeParam delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithVectorParamAndStackReturn: {
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

                final Collection<? extends CharSequence> returnValue = delegate.methodWithVectorParamAndStackReturn(objectListCollection);
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
