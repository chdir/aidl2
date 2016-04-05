// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IntTest$$AidlClientImpl implements IntTest {
  private final IBinder delegate;

  public IntTest$$AidlClientImpl(IBinder delegate) {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public void methodWithIntParameter(int parameter) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IntTest$$AidlServerImpl.DESCRIPTOR);

      data.writeInt(parameter);

      this.delegate.transact(IntTest$$AidlServerImpl.TRANSACT_methodWithIntParameter, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
