// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.graphics.RectF;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class NonFinalAsAbstract$$AidlClientImpl implements NonFinalAsAbstract {
  private final IBinder delegate;

  public NonFinalAsAbstract$$AidlClientImpl(IBinder delegate) {
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
      data.writeInterfaceToken(NonFinalAsAbstract$$AidlServerImpl.DESCRIPTOR);

      data.writeParcelable(nonFinalParcelableParam, 0);

      delegate.transact(NonFinalAsAbstract$$AidlServerImpl.TRANSACT_testMethod, data, reply, 0);
      reply.readException();

      return reply.readParcelable(getClass().getClassLoader());
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
      data.writeInterfaceToken(NonFinalAsAbstract$$AidlServerImpl.DESCRIPTOR);

      AidlUtil.writeExternalizable(data, nonFinalExParam);

      delegate.transact(NonFinalAsAbstract$$AidlServerImpl.TRANSACT_exMethod, data, reply, 0);
      reply.readException();

      return AidlUtil.readSafeExternalizable(reply);
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
