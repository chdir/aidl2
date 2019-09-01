// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class StringTest2$$AidlClientImpl implements StringTest2 {
  private final IBinder delegate;

  public StringTest2$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public String methodWithStringReturn() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(StringTest2$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(StringTest2$$AidlServerImpl.TRANSACT_methodWithStringReturn, data, reply, 0);
      reply.readException();

      return reply.readString();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
