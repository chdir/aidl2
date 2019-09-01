// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

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
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
@SuppressWarnings("unchecked")
public final class AdjustingTransactionIds$$AidlClientImpl implements AdjustingTransactionIds {
  private final IBinder delegate;

  public AdjustingTransactionIds$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  @Call(1)
  public String o() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_o, data, reply, 0);
      reply.readException();

      return reply.readString();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @Call(2)
  public Parcelable someMethod(byte byteParameter) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      data.writeInt(byteParameter);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public Integer anotherMethod(long parameter) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      data.writeLong(parameter);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_anotherMethod, data, reply, 0);
      reply.readException();

      final Integer returnValueTmp;
      if (reply.readByte() == -1) {
        returnValueTmp = null;
      } else {
        returnValueTmp = reply.readInt();
      }
      return returnValueTmp;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @Call(14)
  public void yetAnotherMethod(String parameter) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      data.writeString(parameter);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_yetAnotherMethod, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public Parcelable someMethod(long arg0) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      data.writeLong(arg0);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public Parcelable someMethod(char arg0) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      data.writeInt(arg0);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public Parcelable someMethod2() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod2, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public Parcelable someMethod3(Object[] arg0) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      if (arg0 == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(arg0.length);
        for (Object arg0Component : arg0) {
          data.writeValue(arg0Component);
        }
      }

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod3, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public <T> Parcelable someMethod4(T[] arg0) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      if (arg0 == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(arg0.length);
        for (Object arg0Component : arg0) {
          data.writeValue(arg0Component);
        }
      }

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod4, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public void yetAnotherMethod() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_yetAnotherMethod, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public void yetAnotherMethod(String arg0, String arg1) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      data.writeString(arg0);

      data.writeString(arg1);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_yetAnotherMethod, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @Call(16777215)
  public Parcelable someMethod(int[] intArrayParam) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      data.writeIntArray(intArrayParam);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @SuppressWarnings("aidl2")
  @Call(1598835019)
  public Parcelable someMethod() throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(AdjustingTransactionIds$$AidlServerImpl.DESCRIPTOR);

      delegate.transact(AdjustingTransactionIds$$AidlServerImpl.TRANSACT_someMethod, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
