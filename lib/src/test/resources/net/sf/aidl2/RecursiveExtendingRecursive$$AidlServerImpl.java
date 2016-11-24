// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class RecursiveExtendingRecursive$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.RecursiveExtendingRecursive";

  static final int TRANSACT_call = IBinder.FIRST_CALL_TRANSACTION;

  static final int TRANSACT_someMethod = IBinder.FIRST_CALL_TRANSACTION + 9000;

  private final RecursiveExtendingRecursive delegate;

  public RecursiveExtendingRecursive$$AidlServerImpl(RecursiveExtendingRecursive delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_call: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final RecursiveExtendingRecursive returnValue = delegate.call();
        reply.writeNoException();

        reply.writeStrongBinder(returnValue == null ? null : returnValue.asBinder());

        return true;
      } case TRANSACT_someMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Parcelable returnValue = delegate.someMethod();
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
