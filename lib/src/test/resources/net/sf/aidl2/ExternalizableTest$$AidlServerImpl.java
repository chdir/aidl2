// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ExternalizableTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ExternalizableTest";

    static final int TRANSACT_methodWithExternalizableParameter = IBinder.FIRST_CALL_TRANSACTION + 0;

    private final ExternalizableTest delegate;

    public ExternalizableTest$$AidlServerImpl(ExternalizableTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithExternalizableParameter: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final SomeExternalizable externalizableTmp;
                if (data.readByte() == -1) {
                    externalizableTmp = null;
                } else {
                    ObjectInputStream objectInputStream = null;
                    SomeExternalizable externalizableExternalizable = null;
                    try {
                        objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data.createByteArray()));
                        externalizableExternalizable = new SomeExternalizable();
                        externalizableExternalizable.readExternal(objectInputStream);
                    } catch (Exception e) {
                        data.writeException(new IllegalStateException("Failed to deserialize net.sf.aidl2.SomeExternalizable", e));
                        return true;
                    } finally {
                        AidlUtil.shut(objectInputStream);
                    }
                    externalizableTmp = externalizableExternalizable;
                }

                this.delegate.methodWithExternalizableParameter(externalizableTmp);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
