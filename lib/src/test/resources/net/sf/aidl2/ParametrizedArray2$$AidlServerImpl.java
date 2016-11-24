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
public final class ParametrizedArray2$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.ParametrizedArray2";

  static final int TRANSACT_methodWithParametrizedArrayParam = IBinder.FIRST_CALL_TRANSACTION;

  private final ParametrizedArray2 delegate;

  public ParametrizedArray2$$AidlServerImpl(ParametrizedArray2 delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_methodWithParametrizedArrayParam: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Parametrized<String>[] returnValue = delegate.methodWithParametrizedArrayParam();
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.length);
          for (Parametrized<String> returnValueComponent : returnValue) {
            reply.writeParcelable(returnValueComponent, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
        }

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
