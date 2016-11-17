// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
@SuppressWarnings("unchecked")
public final class TransactionIdOrder$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.TransactionIdOrder";

  static final int TRANSACT_yetAnotherMethod = IBinder.FIRST_CALL_TRANSACTION + 0;

  static final int TRANSACT_anotherMethod = IBinder.FIRST_CALL_TRANSACTION + 1;

  static final int TRANSACT_someMethod = IBinder.FIRST_CALL_TRANSACTION + 2;

  static final int TRANSACT_someMethod_ = IBinder.FIRST_CALL_TRANSACTION + 3;

  static final int TRANSACT_someMethod__ = IBinder.FIRST_CALL_TRANSACTION + 4;

  static final int TRANSACT_someMethod___ = IBinder.FIRST_CALL_TRANSACTION + 9001;

  static final int TRANSACT_someMethod____ = IBinder.FIRST_CALL_TRANSACTION + 9002;

  static final int TRANSACT_someMethod2 = IBinder.FIRST_CALL_TRANSACTION + 9003;

  static final int TRANSACT_someMethod3 = IBinder.FIRST_CALL_TRANSACTION + 9004;

  static final int TRANSACT_someMethod4 = IBinder.FIRST_CALL_TRANSACTION + 9005;

  static final int TRANSACT_yetAnotherMethod_ = IBinder.FIRST_CALL_TRANSACTION + 9006;

  static final int TRANSACT_yetAnotherMethod__ = IBinder.FIRST_CALL_TRANSACTION + 9007;

  private final TransactionIdOrder delegate;

  public TransactionIdOrder$$AidlServerImpl(TransactionIdOrder delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_yetAnotherMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final String parameter = data.readString();

        delegate.yetAnotherMethod(parameter);
        reply.writeNoException();

        return true;
      } case TRANSACT_anotherMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final long parameter = data.readLong();

        final Integer returnValue = delegate.anotherMethod(parameter);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeByte((byte) -1);
        } else {
          reply.writeByte((byte) 0);
          reply.writeInt(returnValue);
        }

        return true;
      } case TRANSACT_someMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Parcelable returnValue = delegate.someMethod();
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_someMethod_: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final byte byteParameter = (byte) data.readInt();

        final Parcelable returnValue = delegate.someMethod(byteParameter);
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_someMethod__: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final int[] intArrayParam = data.createIntArray();

        final Parcelable returnValue = delegate.someMethod(intArrayParam);
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_someMethod___: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final long arg0 = data.readLong();

        final Parcelable returnValue = delegate.someMethod(arg0);
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_someMethod____: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final char arg0 = (char) data.readInt();

        final Parcelable returnValue = delegate.someMethod(arg0);
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_someMethod2: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Parcelable returnValue = delegate.someMethod2();
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_someMethod3: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Object[] arg0Array;
        final int arg0Length = data.readInt();
        if (arg0Length < 0) {
          arg0Array = null;
        } else {
          arg0Array = new Object[arg0Length];
          for (int i = 0; i < arg0Array.length; i++) {
            arg0Array[i] = data.readValue(getClass().getClassLoader());
          }
        }

        final Parcelable returnValue = delegate.someMethod3(arg0Array);
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_someMethod4: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Object[] arg0Array;
        final int arg0Length = data.readInt();
        if (arg0Length < 0) {
          arg0Array = null;
        } else {
          arg0Array = new Object[arg0Length];
          for (int i = 0; i < arg0Array.length; i++) {
            arg0Array[i] = data.readValue(getClass().getClassLoader());
          }
        }

        final Parcelable returnValue = delegate.someMethod4(AidlUtil.unsafeCast(arg0Array));
        reply.writeNoException();

        reply.writeParcelable(returnValue, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_yetAnotherMethod_: {
        data.enforceInterface(this.getInterfaceDescriptor());

        delegate.yetAnotherMethod();
        reply.writeNoException();

        return true;
      } case TRANSACT_yetAnotherMethod__: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final String arg0 = data.readString();

        final String arg1 = data.readString();

        delegate.yetAnotherMethod(arg0, arg1);
        reply.writeNoException();

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
