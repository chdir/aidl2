// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.FileOutputStream;
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
public final class ConverterTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ConverterTest";

    static final int TRANSACT_method = IBinder.FIRST_CALL_TRANSACTION;

    private final ConverterTest delegate;

    private final StreamConverter streamConverter = new StreamConverter();

    public ConverterTest$$AidlServerImpl(ConverterTest delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_method: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final FileOutputStream arg = streamConverter.read(FileOutputStream.class, data);

                delegate.method(arg);
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}