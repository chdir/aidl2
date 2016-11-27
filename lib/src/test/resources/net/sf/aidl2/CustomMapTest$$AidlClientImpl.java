// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.Map;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class CustomMapTest$$AidlClientImpl implements CustomMapTest {
  private final IBinder delegate;

  public CustomMapTest$$AidlClientImpl(IBinder delegate) {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public DumbMap mapTest(DumbMap[] dumbMap) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(CustomMapTest$$AidlServerImpl.DESCRIPTOR);

      if (dumbMap == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(dumbMap.length);
        for (DumbMap dumbMapComponent : dumbMap) {
          if (dumbMapComponent == null) {
            data.writeInt(-1);
          } else {
            data.writeInt(dumbMapComponent.size());
            for (Map.Entry<String, ?> dumbMapComponentEntry: dumbMapComponent.entrySet()) {
              data.writeString(dumbMapComponentEntry.getKey());
            }
          }
        }
      }

      delegate.transact(CustomMapTest$$AidlServerImpl.TRANSACT_mapTest, data, reply, 0);
      reply.readException();

      final DumbMap returnValueMap;
      final int returnValueSize = reply.readInt();
      if (returnValueSize < 0) {
        returnValueMap = null;
      } else {
        returnValueMap = new DumbMap();
        for (int k = 0; k < returnValueSize; k++) {
          returnValueMap.put(reply.readString(), null);
        }
      }
      return returnValueMap;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
