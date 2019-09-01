// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.graphics.RectF;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MapAndNestedTypeArgs$$AidlClientImpl implements MapAndNestedTypeArgs {
  private final IBinder delegate;

  public MapAndNestedTypeArgs$$AidlClientImpl(IBinder delegate) throws RemoteException {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public <T extends List<Parcelable>> void methodWithNestedRecType(Map<RectF, T> weirdMap_) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(MapAndNestedTypeArgs$$AidlServerImpl.DESCRIPTOR);

      if (weirdMap_ == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(weirdMap_.size());
        for (Map.Entry<? extends Parcelable, ? extends Collection<? extends Parcelable>> weirdMap_Entry: weirdMap_.entrySet()) {
          if (weirdMap_Entry.getKey() == null) {
            data.writeByte((byte) -1);
          } else {
            data.writeByte((byte) 0);
            weirdMap_Entry.getKey().writeToParcel(data, 0);
          }
          if (weirdMap_Entry.getValue() == null) {
            data.writeInt(-1);
          } else {
            data.writeInt(weirdMap_Entry.getValue().size());
            for (Parcelable weirdMap_Entry_getValue__Element : weirdMap_Entry.getValue()) {
              data.writeParcelable(weirdMap_Entry_getValue__Element, 0);
            }
          }
        }
      }

      delegate.transact(MapAndNestedTypeArgs$$AidlServerImpl.TRANSACT_methodWithNestedRecType, data, reply, 0);
      reply.readException();
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
