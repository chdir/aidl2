// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.graphics.RectF;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.io.Externalizable;
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
public final class NonFinalAsAbstract$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.NonFinalAsAbstract";

  static final int TRANSACT_testMethod = IBinder.FIRST_CALL_TRANSACTION + 0;

  static final int TRANSACT_exMethod = IBinder.FIRST_CALL_TRANSACTION + 1;

  private final NonFinalAsAbstract delegate;

  public NonFinalAsAbstract$$AidlServerImpl(NonFinalAsAbstract delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_testMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final RectF nonFinalParcelableParam = data.readParcelable(getClass().getClassLoader());

        final Parcelable returnValue = delegate.testMethod(nonFinalParcelableParam);
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_exMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final SomeNonFinalExternalizable nonFinalExParam = AidlUtil.readFromObjectStream(data);

        final Externalizable returnValue = delegate.exMethod(nonFinalExParam);
        reply.writeNoException();

        AidlUtil.writeToObjectStream(reply, returnValue);

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
