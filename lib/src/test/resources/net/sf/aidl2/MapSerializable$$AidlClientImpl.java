// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.Date;
import java.util.HashMap;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MapSerializable$$AidlClientImpl<T extends HashMap<Integer, Serializable>> implements MapSerializable<T> {
    private final IBinder delegate;

    public MapSerializable$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public HashMap<String, Date> maps(T arg) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(MapSerializable$$AidlServerImpl.DESCRIPTOR);

            AidlUtil.writeToObjectStream(data, arg);

            delegate.transact(MapSerializable$$AidlServerImpl.TRANSACT_maps, data, reply, 0);
            reply.readException();

            return AidlUtil.readFromObjectStream(reply);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}