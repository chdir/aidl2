// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.SizeF;
import java.lang.Deprecated;
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
public final class AbstractListTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.AbstractListTest";

    static final int TRANSACT_methodWithCollectionReturn = IBinder.FIRST_CALL_TRANSACTION;

    private final AbstractListTest delegate;

    public AbstractListTest$$AidlServerImpl(AbstractListTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithCollectionReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final ArrayList<SizeF> abstrListCollection;
                final int abstrListSize = data.readInt();
                if (abstrListSize < 0) {
                    abstrListCollection = null;
                } else {
                    abstrListCollection = new ArrayList<>(abstrListSize);
                    for (int j = 0; j < abstrListSize; j++) {
                        final SizeF abstrListTmp;
                        if (data.readByte() == -1) {
                            abstrListTmp = null;
                        } else {
                            abstrListTmp = data.readSizeF();
                        }
                        abstrListCollection.add(abstrListTmp);
                    }
                }

                final Collection<AbstractListTest> returnValue = delegate.methodWithCollectionReturn(abstrListCollection);
                reply.writeNoException();

                if (returnValue == null) {
                    reply.writeInt(-1);
                } else {
                    reply.writeInt(returnValue.size());
                    for (AbstractListTest returnValueElement : returnValue) {
                        reply.writeStrongBinder(returnValueElement == null ? null : returnValueElement.asBinder());
                    }
                }

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
