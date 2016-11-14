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
public final class ParametrizedArray$$AidlClientImpl implements ParametrizedArray {
  private final IBinder delegate;

  public ParametrizedArray$$AidlClientImpl(IBinder delegate) {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public void methodWithParametrizedArrayParam(Parametrized<String>[] parametrizedArray) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(ParametrizedArray$$AidlServerImpl.DESCRIPTOR);

      if (parametrizedArray == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(parametrizedArray.length);
        for (Parametrized<String> parametrizedArrayComponent : parametrizedArray) {
          data.writeParcelable(parametrizedArrayComponent, 0);
        }
      }

      delegate.transact(ParametrizedArray$$AidlServerImpl.TRANSACT_methodWithParametrizedArrayParam, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
