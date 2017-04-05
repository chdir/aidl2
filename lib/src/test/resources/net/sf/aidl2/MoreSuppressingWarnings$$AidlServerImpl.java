// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collection;
import java.util.HashMap;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class MoreSuppressingWarnings$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.MoreSuppressingWarnings";

  static final int TRANSACT_aMethod = IBinder.FIRST_CALL_TRANSACTION;

  static final int TRANSACT_objectReturn = IBinder.FIRST_CALL_TRANSACTION + 1;

  static final int TRANSACT_rawTypes = IBinder.FIRST_CALL_TRANSACTION + 2;

  private final MoreSuppressingWarnings delegate;

  public MoreSuppressingWarnings$$AidlServerImpl(MoreSuppressingWarnings delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  @SuppressWarnings({
      "chirpkippy",
      "all",
      "wuhahahaha",
      "foobar"
  })
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_aMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        delegate.aMethod();
        reply.writeNoException();

        return true;
      } case TRANSACT_objectReturn: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final Object returnValue = delegate.objectReturn();
        reply.writeNoException();

        reply.writeValue(returnValue);

        return true;
      } case TRANSACT_rawTypes: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final HashMap<Object, Object> rawSetMap;
        final int rawSetSize = data.readInt();
        if (rawSetSize < 0) {
          rawSetMap = null;
        } else {
          rawSetMap = new HashMap<>();
          for (int k = 0; k < rawSetSize; k++) {
            rawSetMap.put(data.readValue(getClass().getClassLoader()), data.readValue(getClass().getClassLoader()));
          }
        }

        final Collection<Object> returnValue = delegate.rawTypes(rawSetMap);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeInt(-1);
        } else {
          reply.writeInt(returnValue.size());
          for (Object returnValueElement : returnValue) {
            reply.writeValue(returnValueElement);
          }
        }

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
