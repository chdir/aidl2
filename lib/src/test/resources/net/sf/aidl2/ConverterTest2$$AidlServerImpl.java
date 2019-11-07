// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ConverterTest2$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.ConverterTest2";

    static final int TRANSACT_method = IBinder.FIRST_CALL_TRANSACTION;

    private final ConverterTest2 delegate;

    private final ListConverter listConverter = new ListConverter();

    public ConverterTest2$$AidlServerImpl(ConverterTest2 delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_method: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final ArrayList<String> returnValue = delegate.method();
                reply.writeNoException();

                listConverter.write(returnValue, reply);

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}