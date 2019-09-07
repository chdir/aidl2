// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ListAndNestedTypeArgs$$AidlClientImpl implements ListAndNestedTypeArgs {
  private final IBinder delegate;

  public ListAndNestedTypeArgs$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public <T extends Callable<T>, U extends ArrayList<Recursive<T>>> U methodWithNestedRecType(U nastyList) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(ListAndNestedTypeArgs$$AidlServerImpl.DESCRIPTOR);

      if (nastyList == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(nastyList.size());
        for (Recursive nastyListElement : nastyList) {
          data.writeStrongBinder(nastyListElement == null ? null : nastyListElement.asBinder());
        }
      }

      delegate.transact(ListAndNestedTypeArgs$$AidlServerImpl.TRANSACT_methodWithNestedRecType, data, reply, 0);
      reply.readException();

      final ArrayList returnValueCollection;
      final int returnValueSize = reply.readInt();
      if (returnValueSize < 0) {
        returnValueCollection = null;
      } else {
        returnValueCollection = new ArrayList(returnValueSize);
        for (int j = 0; j < returnValueSize; j++) {
          final IBinder returnValueBinder = reply.readStrongBinder();
          final Recursive iReturnValue = returnValueBinder == null ? null : InterfaceLoader.asInterface(returnValueBinder, Recursive.class);
          returnValueCollection.add(iReturnValue);
        }
      }
      return (U) returnValueCollection;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
