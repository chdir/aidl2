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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class IpcVersionStability2$$AidlClientImpl implements IpcVersionStability2 {
  private final IBinder delegate;

  public IpcVersionStability2$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
    AidlUtil.verify(delegate, IpcVersionStability2$$AidlServerImpl.DESCRIPTOR, IpcVersionStability2$$AidlServerImpl.ipcVersionId);
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  @Call(1)
  public char test(long i) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability2$$AidlServerImpl.DESCRIPTOR);

      data.writeLong(i);

      delegate.transact(IpcVersionStability2$$AidlServerImpl.TRANSACT_test, data, reply, 0);
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
      data.writeInterfaceToken(IpcVersionStability2$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(IpcVersionStability2$$AidlServerImpl.TRANSACT_sayHello, data, reply, 0);
      reply.readException();

      return reply.readString();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public List<String> test(Set<String> alsoTest) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability2$$AidlServerImpl.DESCRIPTOR);

      if (alsoTest == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(alsoTest.size());
        for (String alsoTestElement : alsoTest) {
          data.writeString(alsoTestElement);
        }
      }

      delegate.transact(IpcVersionStability2$$AidlServerImpl.TRANSACT_test, data, reply, 0);
      reply.readException();

      final ArrayList<String> returnValueCollection;
      final int returnValueSize = reply.readInt();
      if (returnValueSize < 0) {
        returnValueCollection = null;
      } else {
        returnValueCollection = new ArrayList<>(returnValueSize);
        for (int j = 0; j < returnValueSize; j++) {
          returnValueCollection.add(reply.readString());
        }
      }
      return returnValueCollection;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public void method() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability2$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(IpcVersionStability2$$AidlServerImpl.TRANSACT_method, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @NotNull
  public Integer method(int intParam) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability2$$AidlServerImpl.DESCRIPTOR);

      data.writeInt(intParam);

      delegate.transact(IpcVersionStability2$$AidlServerImpl.TRANSACT_method, data, reply, 0);
      reply.readException();

      return reply.readInt();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public <T extends Date> void alsoMapTestButDifferentlyNamed(LinkedHashMap<Parcelable, T> mapArg) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(IpcVersionStability2$$AidlServerImpl.DESCRIPTOR);

      if (mapArg == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(mapArg.size());
        for (Map.Entry<Parcelable, ? extends Serializable> mapArgEntry: mapArg.entrySet()) {
          data.writeParcelable(mapArgEntry.getKey(), 0);
          AidlUtil.writeToObjectStream(data, mapArgEntry.getValue());
        }
      }

      delegate.transact(IpcVersionStability2$$AidlServerImpl.TRANSACT_alsoMapTestButDifferentlyNamed, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
