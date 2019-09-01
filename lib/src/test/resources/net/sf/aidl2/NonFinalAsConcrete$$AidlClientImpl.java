// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.graphics.RectF;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Deprecated;
import java.lang.Override;
import org.jetbrains.annotations.NotNull;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class NonFinalAsConcrete$$AidlClientImpl implements NonFinalAsConcrete {
  private final IBinder delegate;

  public NonFinalAsConcrete$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public RectF testMethod(RectF nonFinalParcelableParam) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(NonFinalAsConcrete$$AidlServerImpl.DESCRIPTOR);

      if (nonFinalParcelableParam == null) {
        data.writeByte((byte) -1);
      } else {
        data.writeByte((byte) 0);
        nonFinalParcelableParam.writeToParcel(data, 0);
      }

      delegate.transact(NonFinalAsConcrete$$AidlServerImpl.TRANSACT_testMethod, data, reply, 0);
      reply.readException();

      final RectF returnValueTmp;
      if (reply.readByte() == -1) {
        returnValueTmp = null;
      } else {
        returnValueTmp = RectF.CREATOR.createFromParcel(reply);
      }
      return returnValueTmp;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @NotNull
  public RectF allNotNullTestMethod(@NotNull RectF nonFinalParcelableParam) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(NonFinalAsConcrete$$AidlServerImpl.DESCRIPTOR);

      nonFinalParcelableParam.writeToParcel(data, 0);

      delegate.transact(NonFinalAsConcrete$$AidlServerImpl.TRANSACT_allNotNullTestMethod, data, reply, 0);
      reply.readException();

      return RectF.CREATOR.createFromParcel(reply);
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public SomeNonFinalExternalizable exMethod(SomeNonFinalExternalizable nonFinalExParam) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(NonFinalAsConcrete$$AidlServerImpl.DESCRIPTOR);

      if (nonFinalExParam == null) {
        data.writeByte((byte) -1);
      } else {
        data.writeByte((byte) 0);
        ObjectOutputStream objectOutputStream = null;
        try {
          ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
          objectOutputStream = new ObjectOutputStream(arrayOutputStream);
          nonFinalExParam.writeExternal(objectOutputStream);
          objectOutputStream.flush();
          data.writeByteArray(arrayOutputStream.toByteArray());
        } catch (Exception e) {
          throw new IllegalStateException("Failed to serialize net.sf.aidl2.SomeNonFinalExternalizable", e);
        } finally {
          AidlUtil.shut(objectOutputStream);
        }
      }

      delegate.transact(NonFinalAsConcrete$$AidlServerImpl.TRANSACT_exMethod, data, reply, 0);
      reply.readException();

      final SomeNonFinalExternalizable returnValueTmp;
      if (reply.readByte() == -1) {
        returnValueTmp = null;
      } else {
        ObjectInputStream objectInputStream = null;
        SomeNonFinalExternalizable returnValueExternalizable = null;
        try {
          objectInputStream = new ObjectInputStream(new ByteArrayInputStream(reply.createByteArray()));
          returnValueExternalizable = new SomeNonFinalExternalizable();
          returnValueExternalizable.readExternal(objectInputStream);
        } catch (Exception e) {
          throw new IllegalStateException("Failed to deserialize net.sf.aidl2.SomeNonFinalExternalizable", e);
        } finally {
          AidlUtil.shut(objectInputStream);
        }
        returnValueTmp = returnValueExternalizable;
      }
      return returnValueTmp;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  @NotNull
  public SomeNonFinalExternalizable notNullExMethod(@NotNull SomeNonFinalExternalizable nonFinalExParam) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(NonFinalAsConcrete$$AidlServerImpl.DESCRIPTOR);

      ObjectOutputStream objectOutputStream = null;
      try {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(arrayOutputStream);
        nonFinalExParam.writeExternal(objectOutputStream);
        objectOutputStream.flush();
        data.writeByteArray(arrayOutputStream.toByteArray());
      } catch (Exception e) {
        throw new IllegalStateException("Failed to serialize net.sf.aidl2.SomeNonFinalExternalizable", e);
      } finally {
        AidlUtil.shut(objectOutputStream);
      }

      delegate.transact(NonFinalAsConcrete$$AidlServerImpl.TRANSACT_notNullExMethod, data, reply, 0);
      reply.readException();

      ObjectInputStream objectInputStream = null;
      SomeNonFinalExternalizable returnValueExternalizable = null;
      try {
        objectInputStream = new ObjectInputStream(new ByteArrayInputStream(reply.createByteArray()));
        returnValueExternalizable = new SomeNonFinalExternalizable();
        returnValueExternalizable.readExternal(objectInputStream);
      } catch (Exception e) {
        throw new IllegalStateException("Failed to deserialize net.sf.aidl2.SomeNonFinalExternalizable", e);
      } finally {
        AidlUtil.shut(objectInputStream);
      }
      return returnValueExternalizable;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
