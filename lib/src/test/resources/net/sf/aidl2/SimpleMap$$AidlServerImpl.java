// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
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
import java.util.Map;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class SimpleMap$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.SimpleMap";

  static final int TRANSACT_abstractMapMethod = IBinder.FIRST_CALL_TRANSACTION + 0;

  static final int TRANSACT_concreteMapMethod = IBinder.FIRST_CALL_TRANSACTION + 1;

  private final SimpleMap delegate;

  public SimpleMap$$AidlServerImpl(SimpleMap delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_abstractMapMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final HashMap<Parcelable, SizeF> responderMap;
        final int responderSize = data.readInt();
        if (responderSize < 0) {
          responderMap = null;
        } else {
          responderMap = new HashMap<>();
          for (int k = 0; k < responderSize; k++) {
            final SizeF responderTmp;
            if (data.readByte() == -1) {
              responderTmp = null;
            } else {
              responderTmp = data.readSizeF();
            }
            responderMap.put(data.readParcelable(getClass().getClassLoader()), responderTmp);
          }
        }

        final Map<String, Integer> returnValue = delegate.abstractMapMethod(responderMap);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.size());
          for (Map.Entry<String, Integer> returnValueEntry: returnValue.entrySet()) {
            reply.writeString(returnValueEntry.getKey());
            if (returnValueEntry.getValue() == null) {
              reply.writeByte((byte) -1);
            } else {
              reply.writeByte((byte) 0);
              reply.writeInt(returnValueEntry.getValue());
            }
          }
        }

        return true;
      } case TRANSACT_concreteMapMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final HashMap<Parcelable, SizeF> responderMap;
        final int responderSize = data.readInt();
        if (responderSize < 0) {
          responderMap = null;
        } else {
          responderMap = new HashMap<>(responderSize, 1f);
          for (int k = 0; k < responderSize; k++) {
            final SizeF responderTmp;
            if (data.readByte() == -1) {
              responderTmp = null;
            } else {
              responderTmp = data.readSizeF();
            }
            responderMap.put(data.readParcelable(getClass().getClassLoader()), responderTmp);
          }
        }

        final Map<String, Integer> returnValue = delegate.concreteMapMethod(responderMap);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.size());
          for (Map.Entry<String, Integer> returnValueEntry: returnValue.entrySet()) {
            reply.writeString(returnValueEntry.getKey());
            if (returnValueEntry.getValue() == null) {
              reply.writeByte((byte) -1);
            } else {
              reply.writeByte((byte) 0);
              reply.writeInt(returnValueEntry.getValue());
            }
          }
        }

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
