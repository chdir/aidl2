// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IpcVersionStability2$$AidlServerImpl extends Binder {
  static final long ipcVersionId = 7492761756905359574L;

  static final String DESCRIPTOR = "net.sf.aidl2.IpcVersionStability";

  static final int TRANSACT_test = IBinder.FIRST_CALL_TRANSACTION;

  static final int TRANSACT_sayHello = IBinder.FIRST_CALL_TRANSACTION + 1;

  static final int TRANSACT_test_ = IBinder.FIRST_CALL_TRANSACTION + 2;

  static final int TRANSACT_method = IBinder.FIRST_CALL_TRANSACTION + 3;

  static final int TRANSACT_method_ = IBinder.FIRST_CALL_TRANSACTION + 4;

  static final int TRANSACT_alsoMapTestButDifferentlyNamed = IBinder.FIRST_CALL_TRANSACTION + 5;

  private final IpcVersionStability2 delegate;

  public IpcVersionStability2$$AidlServerImpl(IpcVersionStability2 delegate) {
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

        final HashSet<String> alsoTestCollection;
        final int alsoTestSize = data.readInt();
        if (alsoTestSize < 0) {
          alsoTestCollection = null;
        } else {
          alsoTestCollection = new HashSet<>(alsoTestSize);
          for (int j = 0; j < alsoTestSize; j++) {
            alsoTestCollection.add(data.readString());
          }
        }

        final Collection<String> returnValue = delegate.test(alsoTestCollection);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.size());
          for (String returnValueElement : returnValue) {
            reply.writeString(returnValueElement);
          }
        }

        return true;
      } case TRANSACT_method: {
        data.enforceInterface(this.getInterfaceDescriptor());

        delegate.method();
        reply.writeNoException();

        return true;
      } case TRANSACT_method_: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final int intParam = data.readInt();

        final Integer returnValue = delegate.method(intParam);
        reply.writeNoException();

        reply.writeInt(returnValue);

        return true;
      } case TRANSACT_alsoMapTestButDifferentlyNamed: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final LinkedHashMap<Parcelable, Date> mapArgMap;
        final int mapArgSize = data.readInt();
        if (mapArgSize < 0) {
          mapArgMap = null;
        } else {
          mapArgMap = new LinkedHashMap<>(mapArgSize, 1f);
          for (int k = 0; k < mapArgSize; k++) {
            mapArgMap.put(data.readParcelable(getClass().getClassLoader()), AidlUtil.readFromObjectStream(data));
          }
        }

        delegate.alsoMapTestButDifferentlyNamed((LinkedHashMap) mapArgMap);
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
