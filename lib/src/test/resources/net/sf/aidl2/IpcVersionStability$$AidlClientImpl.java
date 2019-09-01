// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

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
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IpcVersionStability$$AidlClientImpl implements IpcVersionStability {
  private final IBinder delegate;

  public IpcVersionStability$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
    AidlUtil.verify(delegate, IpcVersionStability$$AidlServerImpl.DESCRIPTOR, IpcVersionStability$$AidlServerImpl.ipcVersionId);
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public char test(long i) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability$$AidlServerImpl.DESCRIPTOR);

      data.writeLong(i);

      delegate.transact(IpcVersionStability$$AidlServerImpl.TRANSACT_test, data, reply, 0);
      reply.readException();

      return (char) reply.readInt();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public String sayHello() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(IpcVersionStability$$AidlServerImpl.TRANSACT_sayHello, data, reply, 0);
      reply.readException();

      return reply.readString();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public String[] test(String[] alsoTest) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability$$AidlServerImpl.DESCRIPTOR);

      if (alsoTest == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(alsoTest.length);
        for (String alsoTestComponent : alsoTest) {
          data.writeString(alsoTestComponent);
        }
      }

      delegate.transact(IpcVersionStability$$AidlServerImpl.TRANSACT_test, data, reply, 0);
      reply.readException();

      final String[] returnValueArray;
      final int returnValueLength = reply.readInt();
      if (returnValueLength < 0) {
        returnValueArray = null;
      } else {
        returnValueArray = new String[returnValueLength];
        for (int i = 0; i < returnValueArray.length; i++) {
          returnValueArray[i] = reply.readString();
        }
      }
      return returnValueArray;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public Void method(Void nothing) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(IpcVersionStability$$AidlServerImpl.TRANSACT_method, data, reply, 0);
      reply.readException();

      return null;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public int method(@NotNull Integer intParam) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability$$AidlServerImpl.DESCRIPTOR);

      data.writeInt(intParam);

      delegate.transact(IpcVersionStability$$AidlServerImpl.TRANSACT_method, data, reply, 0);
      reply.readException();

      return reply.readInt();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public void mapTest(Map<? extends Parcelable, Serializable> m) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability$$AidlServerImpl.DESCRIPTOR);

      if (m == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(m.size());
        for (Map.Entry<? extends Parcelable, Serializable> mEntry: m.entrySet()) {
          data.writeParcelable(mEntry.getKey(), 0);
          AidlUtil.writeToObjectStream(data, mEntry.getValue());
        }
      }

      delegate.transact(IpcVersionStability$$AidlServerImpl.TRANSACT_mapTest, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
