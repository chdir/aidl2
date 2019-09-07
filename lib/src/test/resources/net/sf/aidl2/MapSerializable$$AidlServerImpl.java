// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MapSerializable$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.MapSerializable";

    static final int TRANSACT_maps = IBinder.FIRST_CALL_TRANSACTION;

    private final MapSerializable delegate;

    public MapSerializable$$AidlServerImpl(MapSerializable delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_maps: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final HashMap<Integer, Serializable> arg = AidlUtil.readFromObjectStream(data);

                final Serializable returnValue = delegate.maps(AidlUtil.unsafeCast(arg));
                reply.writeNoException();

                AidlUtil.writeToObjectStream(reply, returnValue);

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}