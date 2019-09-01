// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MoreSuppressingWarnings$$AidlClientImpl implements MoreSuppressingWarnings {
  private final IBinder delegate;

  public MoreSuppressingWarnings$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  @SuppressWarnings({
      "wuhahahaha",
      "chirpkippy"
  })
  public void aMethod() throws IllegalArgumentException, RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(MoreSuppressingWarnings$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(MoreSuppressingWarnings$$AidlServerImpl.TRANSACT_aMethod, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @SuppressWarnings({
      "foobar",
      "all"
  })
  public Object objectReturn() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(MoreSuppressingWarnings$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(MoreSuppressingWarnings$$AidlServerImpl.TRANSACT_objectReturn, data, reply, 0);
      reply.readException();

      return reply.readValue(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @SuppressWarnings("all")
  public List rawTypes(Map rawSet) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(MoreSuppressingWarnings$$AidlServerImpl.DESCRIPTOR);

      if (rawSet == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(rawSet.size());
        for (Map.Entry<?, ?> rawSetEntry : AidlUtil.<Set<Map.Entry<Object, Object>>>unsafeCast(rawSet.entrySet())) {
          data.writeValue(rawSetEntry.getKey());
          data.writeValue(rawSetEntry.getValue());
        }
      }

      delegate.transact(MoreSuppressingWarnings$$AidlServerImpl.TRANSACT_rawTypes, data, reply, 0);
      reply.readException();

      final ArrayList<Object> returnValueCollection;
      final int returnValueSize = reply.readInt();
      if (returnValueSize < 0) {
        returnValueCollection = null;
      } else {
        returnValueCollection = new ArrayList<>(returnValueSize);
        for (int j = 0; j < returnValueSize; j++) {
          returnValueCollection.add(reply.readValue(getClass().getClassLoader()));
        }
      }
      return returnValueCollection;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
