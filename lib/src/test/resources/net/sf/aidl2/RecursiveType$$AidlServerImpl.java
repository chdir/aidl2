// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
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
public final class RecursiveType$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.RecursiveType";

  static final int TRANSACT_wow = IBinder.FIRST_CALL_TRANSACTION + 0;

  private final RecursiveType delegate;

  public RecursiveType$$AidlServerImpl(RecursiveType delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_wow: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final IBinder troubleBinder = data.readStrongBinder();
        final Recursive iTrouble = troubleBinder == null ? null : InterfaceLoader.asInterface(troubleBinder, Recursive.class);

        delegate.wow(AidlUtil.unsafeCast(iTrouble));
        reply.writeNoException();

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
