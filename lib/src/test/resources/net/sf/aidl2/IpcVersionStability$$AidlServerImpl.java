// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.io.Serializable;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Void;
import java.util.HashMap;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IpcVersionStability$$AidlServerImpl extends Binder {
  static final long ipcVersionId = 7492761756905359574L;

  static final String DESCRIPTOR = "net.sf.aidl2.IpcVersionStability";

  static final int TRANSACT_test = IBinder.FIRST_CALL_TRANSACTION;

  static final int TRANSACT_sayHello = IBinder.FIRST_CALL_TRANSACTION + 1;

  static final int TRANSACT_test_ = IBinder.FIRST_CALL_TRANSACTION + 2;

  static final int TRANSACT_method = IBinder.FIRST_CALL_TRANSACTION + 3;

  static final int TRANSACT_method_ = IBinder.FIRST_CALL_TRANSACTION + 4;

  static final int TRANSACT_mapTest = IBinder.FIRST_CALL_TRANSACTION + 5;

  private final IpcVersionStability delegate;

  public IpcVersionStability$$AidlServerImpl(IpcVersionStability delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_test: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final long i = data.readLong();

        final char returnValue = delegate.test(i);
        reply.writeNoException();

        reply.writeInt(returnValue);

        return true;
      } case TRANSACT_sayHello: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final String returnValue = delegate.sayHello();
        reply.writeNoException();

        reply.writeString(returnValue);

        return true;
      } case TRANSACT_test_: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final String[] alsoTestArray;
        final int alsoTestLength = data.readInt();
        if (alsoTestLength < 0) {
          alsoTestArray = null;
        } else {
          alsoTestArray = new String[alsoTestLength];
          for (int i = 0; i < alsoTestArray.length; i++) {
            alsoTestArray[i] = data.readString();
          }
        }

        final String[] returnValue = delegate.test(alsoTestArray);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.length);
          for (String returnValueComponent : returnValue) {
            reply.writeString(returnValueComponent);
          }
        }

        return true;
      } case TRANSACT_method: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Void nothing = null;

        delegate.method(nothing);
        reply.writeNoException();


        return true;
      } case TRANSACT_method_: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Integer intParam = data.readInt();

        final int returnValue = delegate.method(intParam);
        reply.writeNoException();

        reply.writeInt(returnValue);

        return true;
      } case TRANSACT_mapTest: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final HashMap<Parcelable, Serializable> mMap;
        final int mSize = data.readInt();
        if (mSize < 0) {
          mMap = null;
        } else {
          mMap = new HashMap<>();
          for (int k = 0; k < mSize; k++) {
            mMap.put(data.readParcelable(getClass().getClassLoader()), AidlUtil.readFromObjectStream(data));
          }
        }

        delegate.mapTest(mMap);
        reply.writeNoException();

        return true;
      } case AidlUtil.VERSION_TRANSACTION: {
        data.enforceInterface(this.getInterfaceDescriptor());

        reply.writeNoException();
        reply.writeLong(ipcVersionId);

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
