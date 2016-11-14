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
public final class StringTest$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.StringTest";

  static final int TRANSACT_methodWithStringParameter = IBinder.FIRST_CALL_TRANSACTION + 0;

  private final StringTest delegate;

  public StringTest$$AidlServerImpl(StringTest delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_methodWithStringParameter: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final String stringParam = data.readString();

        delegate.methodWithStringParameter(stringParam);
        reply.writeNoException();

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
