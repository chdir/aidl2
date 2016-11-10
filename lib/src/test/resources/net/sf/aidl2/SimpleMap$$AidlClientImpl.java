// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.SizeF;
import java.lang.Deprecated;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class SimpleMap$$AidlClientImpl implements SimpleMap {
  private final IBinder delegate;

  public SimpleMap$$AidlClientImpl(IBinder delegate) {
    this.delegate = delegate;
  }

  @Override
  public IBinder asBinder() {
    return delegate;
  }

  @Override
  public Map<String, Integer> abstractMapMethod(Map<Parcelable, SizeF> responder) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(SimpleMap$$AidlServerImpl.DESCRIPTOR);

      if (responder == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(responder.size());
        for (Map.Entry<Parcelable, SizeF> responderEntry: responder.entrySet()) {
          data.writeParcelable(responderEntry.getKey(), 0);
          if (responderEntry.getValue() == null) {
            data.writeByte((byte) -1);
          } else {
            data.writeByte((byte) 0);
            data.writeSizeF(responderEntry.getValue());
          }
        }
      }

      delegate.transact(SimpleMap$$AidlServerImpl.TRANSACT_abstractMapMethod, data, reply, 0);
      reply.readException();

      final HashMap<String, Integer> returnValueMap;
      final int returnValueSize = reply.readInt();
      if (returnValueSize < 0) {
        returnValueMap = null;
      } else {
        returnValueMap = new HashMap<>();
        for (int k = 0; k < returnValueSize; k++) {
          final Integer returnValueTmp;
          if (reply.readByte() == -1) {
            returnValueTmp = null;
          } else {
            returnValueTmp = reply.readInt();
          }
          returnValueMap.put(reply.readString(), returnValueTmp);
        }
      }
      return returnValueMap;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }

  @Override
  public LinkedHashMap<String, Integer> concreteMapMethod(HashMap<Parcelable, SizeF> responder) throws RemoteException {
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    try {
      data.writeInterfaceToken(SimpleMap$$AidlServerImpl.DESCRIPTOR);

      if (responder == null) {
        data.writeInt(-1);
      } else {
        data.writeInt(responder.size());
        for (Map.Entry<Parcelable, SizeF> responderEntry: responder.entrySet()) {
          data.writeParcelable(responderEntry.getKey(), 0);
          if (responderEntry.getValue() == null) {
            data.writeByte((byte) -1);
          } else {
            data.writeByte((byte) 0);
            data.writeSizeF(responderEntry.getValue());
          }
        }
      }

      delegate.transact(SimpleMap$$AidlServerImpl.TRANSACT_concreteMapMethod, data, reply, 0);
      reply.readException();

      final LinkedHashMap<String, Integer> returnValueMap;
      final int returnValueSize = reply.readInt();
      if (returnValueSize < 0) {
        returnValueMap = null;
      } else {
        returnValueMap = new LinkedHashMap<>(returnValueSize, 1f);
        for (int k = 0; k < returnValueSize; k++) {
          final Integer returnValueTmp;
          if (reply.readByte() == -1) {
            returnValueTmp = null;
          } else {
            returnValueTmp = reply.readInt();
          }
          returnValueMap.put(reply.readString(), returnValueTmp);
        }
      }
      return returnValueMap;
    } finally {
      data.recycle();
      reply.recycle();
    }
  }
}
