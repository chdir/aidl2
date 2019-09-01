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
public final class ParametrizedArray2$$AidlClientImpl implements ParametrizedArray2 {
  private final IBinder delegate;

  public ParametrizedArray2$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public Parametrized<String>[] methodWithParametrizedArrayParam() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(ParametrizedArray2$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(ParametrizedArray2$$AidlServerImpl.TRANSACT_methodWithParametrizedArrayParam, data, reply, 0);
      reply.readException();

      final Parametrized<?>[] returnValueArray;
      final int returnValueLength = reply.readInt();
      if (returnValueLength < 0) {
        returnValueArray = null;
      } else {
        returnValueArray = new Parametrized<?>[returnValueLength];
        for (int i = 0; i < returnValueArray.length; i++) {
          returnValueArray[i] = reply.readParcelable(getClass().getClassLoader());
        }
      }
      return (Parametrized<String>[]) returnValueArray;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
