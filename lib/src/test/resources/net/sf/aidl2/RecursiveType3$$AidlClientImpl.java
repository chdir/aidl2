// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.Override;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class RecursiveType3$$AidlClientImpl implements RecursiveType3 {
  private final IBinder delegate;

  public RecursiveType3$$AidlClientImpl(IBinder delegate) {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public <T extends Parametrized3<T, Parametrized3<T, Object, ?>, ?>> Parametrized3<T, Object, ?> wow() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(RecursiveType3$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(RecursiveType3$$AidlServerImpl.TRANSACT_wow, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
