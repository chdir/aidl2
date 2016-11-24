// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.graphics.RectF;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class NonFinalAsConcrete$$AidlServerImpl extends Binder {
  static final String DESCRIPTOR = "net.sf.aidl2.NonFinalAsConcrete";

  static final int TRANSACT_testMethod = IBinder.FIRST_CALL_TRANSACTION;

  static final int TRANSACT_allNotNullTestMethod = IBinder.FIRST_CALL_TRANSACTION + 1;

  static final int TRANSACT_exMethod = IBinder.FIRST_CALL_TRANSACTION + 2;

  static final int TRANSACT_notNullExMethod = IBinder.FIRST_CALL_TRANSACTION + 3;

  private final NonFinalAsConcrete delegate;

  public NonFinalAsConcrete$$AidlServerImpl(NonFinalAsConcrete delegate) {
    this.delegate = delegate;

    this.attachInterface(delegate, DESCRIPTOR);
  }

  @Override
  protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
    switch(code) {
      case TRANSACT_testMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final RectF nonFinalParcelableParamTmp;
        if (data.readByte() == -1) {
          nonFinalParcelableParamTmp = null;
        } else {
          nonFinalParcelableParamTmp = RectF.CREATOR.createFromParcel(data);
        }

        final Parcelable returnValue = delegate.testMethod(nonFinalParcelableParamTmp);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeByte((byte) -1);
        } else {
          reply.writeByte((byte) 0);
          returnValue.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }

        return true;
      } case TRANSACT_allNotNullTestMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final RectF nonFinalParcelableParam = RectF.CREATOR.createFromParcel(data);

        final Parcelable returnValue = delegate.allNotNullTestMethod(nonFinalParcelableParam);
        reply.writeNoException();

        returnValue.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);

        return true;
      } case TRANSACT_exMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        final SomeNonFinalExternalizable nonFinalExParamTmp;
        if (data.readByte() == -1) {
          nonFinalExParamTmp = null;
        } else {
          ObjectInputStream objectInputStream = null;
          SomeNonFinalExternalizable nonFinalExParamExternalizable = null;
          try {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data.createByteArray()));
            nonFinalExParamExternalizable = new SomeNonFinalExternalizable();
            nonFinalExParamExternalizable.readExternal(objectInputStream);
          } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize net.sf.aidl2.SomeNonFinalExternalizable", e);
          } finally {
            AidlUtil.shut(objectInputStream);
          }
          nonFinalExParamTmp = nonFinalExParamExternalizable;
        }

        final Externalizable returnValue = delegate.exMethod(nonFinalExParamTmp);
        reply.writeNoException();

        if (returnValue == null) {
          reply.writeByte((byte) -1);
        } else {
          reply.writeByte((byte) 0);
          ObjectOutputStream objectOutputStream = null;
          try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            returnValue.writeExternal(objectOutputStream);
            objectOutputStream.flush();
            reply.writeByteArray(arrayOutputStream.toByteArray());
          } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize net.sf.aidl2.SomeNonFinalExternalizable", e);
          } finally {
            AidlUtil.shut(objectOutputStream);
          }
        }

        return true;
      } case TRANSACT_notNullExMethod: {
        data.enforceInterface(this.getInterfaceDescriptor());

        ObjectInputStream objectInputStream = null;
        SomeNonFinalExternalizable nonFinalExParamExternalizable = null;
        try {
          objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data.createByteArray()));
          nonFinalExParamExternalizable = new SomeNonFinalExternalizable();
          nonFinalExParamExternalizable.readExternal(objectInputStream);
        } catch (Exception e) {
          throw new IllegalStateException("Failed to deserialize net.sf.aidl2.SomeNonFinalExternalizable", e);
        } finally {
          AidlUtil.shut(objectInputStream);
        }

        final Externalizable returnValue = delegate.notNullExMethod(nonFinalExParamExternalizable);
        reply.writeNoException();

        ObjectOutputStream objectOutputStream = null;
        try {
          ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
          objectOutputStream = new ObjectOutputStream(arrayOutputStream);
          returnValue.writeExternal(objectOutputStream);
          objectOutputStream.flush();
          reply.writeByteArray(arrayOutputStream.toByteArray());
        } catch (Exception e) {
          throw new IllegalStateException("Failed to serialize net.sf.aidl2.SomeNonFinalExternalizable", e);
        } finally {
          AidlUtil.shut(objectOutputStream);
        }

        return true;
      }
    }
    return super.onTransact(code, data, reply, flags);
  }
}
