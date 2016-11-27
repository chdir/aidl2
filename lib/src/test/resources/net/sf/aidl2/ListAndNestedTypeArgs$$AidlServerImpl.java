// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class ListAndNestedTypeArgs$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.ListAndNestedTypeArgs";

  static final int TRANSACT_methodWithNestedRecType = IBinder.FIRST_CALL_TRANSACTION;

  private final ListAndNestedTypeArgs delegate;

  public ListAndNestedTypeArgs$$AidlServerImpl(ListAndNestedTypeArgs delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_methodWithNestedRecType: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final ArrayList nastyListCollection;
        final int nastyListSize = data.readInt();
        if (nastyListSize < 0) {
          nastyListCollection = null;
        } else {
          nastyListCollection = new ArrayList(nastyListSize);
          for (int j = 0; j < nastyListSize; j++) {
            final IBinder nastyListBinder = data.readStrongBinder();
            final Recursive iNastyList = nastyListBinder == null ? null : InterfaceLoader.asInterface(nastyListBinder, Recursive.class);
            nastyListCollection.add(iNastyList);
          }
        }

        final Collection<? extends Recursive> returnValue = delegate.methodWithNestedRecType(AidlUtil.unsafeCast(nastyListCollection));
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.size());
          for (Recursive returnValueElement : returnValue) {
            reply.writeStrongBinder(returnValueElement == null ? null : returnValueElement.asBinder());
          }
        }

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
