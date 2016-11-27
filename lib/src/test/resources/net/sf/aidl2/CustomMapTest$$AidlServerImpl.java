// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.util.Map;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class CustomMapTest$$AidlServerImpl extends Binder {
  static final long ipcVersionId = 4038619163579820833L;

  static final String DESCRIPTOR = "net.sf.aidl2.CustomMapTest";

  static final int TRANSACT_mapTest = IBinder.FIRST_CALL_TRANSACTION;

  private final CustomMapTest delegate;

  public CustomMapTest$$AidlServerImpl(CustomMapTest delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_mapTest: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final DumbMap[] dumbMapArray;
        final int dumbMapLength = data.readInt();
        if (dumbMapLength < 0) {
          dumbMapArray = null;
        } else {
          dumbMapArray = new DumbMap[dumbMapLength];
          for (int i = 0; i < dumbMapArray.length; i++) {
            final DumbMap dumbMapMap;
            final int dumbMapSize = data.readInt();
            if (dumbMapSize < 0) {
              dumbMapMap = null;
            } else {
              dumbMapMap = new DumbMap();
              for (int k = 0; k < dumbMapSize; k++) {
                dumbMapMap.put(data.readString(), null);
              }
            }
            dumbMapArray[i] = dumbMapMap;
          }
        }

        final Map<String, ?> returnValue = delegate.mapTest(dumbMapArray);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.size());
          for (Map.Entry<String, ?> returnValueEntry: returnValue.entrySet()) {
            reply.writeString(returnValueEntry.getKey());
          }
        }

        return true;
      } case AidlUtil.VERSION_TRANSACTION: {
        data.enforceInterface(this.getInterfaceDescriptor());

        reply.writeNoException();
        reply.writeLong(ipcVersionId);

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
