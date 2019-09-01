// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class NullabilityInheritance$$AidlClientImpl implements NullabilityInheritance {
  private final IBinder delegate;

  public NullabilityInheritance$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  @Nullable
  public Integer testMethod1(Integer p1, @NotNull Integer p2) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(NullabilityInheritance$$AidlServerImpl.DESCRIPTOR);

      if (p1 == null) {
        data.writeByte((byte) -1);
      } else {
        data.writeByte((byte) 0);
        data.writeInt(p1);
      }

      data.writeInt(p2);

      delegate.transact(NullabilityInheritance$$AidlServerImpl.TRANSACT_testMethod1, data, reply, 0);
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
  public Integer testMethod2(Integer p3) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(NullabilityInheritance$$AidlServerImpl.DESCRIPTOR);

      data.writeInt(p3);

      delegate.transact(NullabilityInheritance$$AidlServerImpl.TRANSACT_testMethod2, data, reply, 0);
      reply.readException();

      return reply.readInt();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
