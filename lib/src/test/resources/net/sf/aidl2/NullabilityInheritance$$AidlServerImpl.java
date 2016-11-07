// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
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
public final class NullabilityInheritance$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.NullabilityInheritance";

  static final int TRANSACT_testMethod1 = IBinder.FIRST_CALL_TRANSACTION + 0;

  static final int TRANSACT_testMethod2 = IBinder.FIRST_CALL_TRANSACTION + 1;

  private final NullabilityInheritance delegate;

  public NullabilityInheritance$$AidlServerImpl(NullabilityInheritance delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_testMethod1: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Integer p1Tmp;
        if (data.readByte() == -1) {
          p1Tmp = null;
        } else {
          p1Tmp = data.readInt();
        }

        final Integer p2 = data.readInt();

        final Integer returnValue = delegate.testMethod1(p1Tmp, p2);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeByte((byte) -1);
        } else {
          reply.writeByte((byte) 0);
          reply.writeInt(returnValue);
        }

        return true;
      } case TRANSACT_testMethod2: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Integer p3 = data.readInt();

        final Integer returnValue = delegate.testMethod2(p3);
        reply.writeNoException();

        reply.writeInt(returnValue);

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
