// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.graphics.RectF;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MapAndNestedTypeArgs$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.MapAndNestedTypeArgs";

  static final int TRANSACT_methodWithNestedRecType = IBinder.FIRST_CALL_TRANSACTION;

  private final MapAndNestedTypeArgs delegate;

  public MapAndNestedTypeArgs$$AidlServerImpl(MapAndNestedTypeArgs delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_methodWithNestedRecType: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final HashMap<RectF, List<Parcelable>> weirdMap_Map;
        final int weirdMap_Size = data.readInt();
        if (weirdMap_Size < 0) {
          weirdMap_Map = null;
        } else {
          weirdMap_Map = new HashMap<>();
          for (int k = 0; k < weirdMap_Size; k++) {
            final RectF weirdMap_Tmp;
            if (data.readByte() == -1) {
              weirdMap_Tmp = null;
            } else {
              weirdMap_Tmp = RectF.CREATOR.createFromParcel(data);
            }
            final ArrayList<Parcelable> weirdMap_Collection;
            final int weirdMap_Size_ = data.readInt();
            if (weirdMap_Size_ < 0) {
              weirdMap_Collection = null;
            } else {
              weirdMap_Collection = new ArrayList<>(weirdMap_Size_);
              for (int j = 0; j < weirdMap_Size_; j++) {
                weirdMap_Collection.add(data.readParcelable(getClass().getClassLoader()));
              }
            }
            weirdMap_Map.put(weirdMap_Tmp, weirdMap_Collection);
          }
        }

        delegate.methodWithNestedRecType((Map) weirdMap_Map);
        reply.writeNoException();

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
