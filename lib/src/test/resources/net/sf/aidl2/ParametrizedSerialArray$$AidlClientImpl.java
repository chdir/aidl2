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
public final class ParametrizedSerialArray$$AidlClientImpl implements ParametrizedSerialArray {
  private final IBinder delegate;

  public ParametrizedSerialArray$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public void methodWithParametrizedArrayParam(Parametrized2<String>[] parametrizedArray) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(ParametrizedSerialArray$$AidlServerImpl.DESCRIPTOR);

      AidlUtil.writeToObjectStream(data, parametrizedArray);

      delegate.transact(ParametrizedSerialArray$$AidlServerImpl.TRANSACT_methodWithParametrizedArrayParam, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
