package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

public class AidlUtil {
    public static final int VERSION_TRANSACTION = IBinder.LAST_CALL_TRANSACTION;

    /**
     * Read Serializable/Externalizable object (or array thereof) from Parcel without making assumptions
     * about their runtime type.
     */
    @SuppressWarnings("unchecked")
    public static @Nullable<X> X readFromObjectStream(@NotNull Parcel parcel) {
        final String typeName = parcel.readString();
        if (typeName == null) {
            return null;
        }

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(parcel.createByteArray()));
            return (X) objectInputStream.readObject();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize " + typeName, e);
        } finally {
            shut(objectInputStream);
        }
    }

    /**
     * Write Serializable/Externalizable object (or array thereof) to Parcel without making assumptions about
     * their runtime type.
     */
    public static void writeToObjectStream(@NotNull Parcel parcel, @Nullable Serializable serializable) {
        writeToObjectStreamInner(parcel, serializable);
    }

    /**
     * Write Externalizable object to Parcel without making assumptions about it's runtime type.
     */
    public static void writeToObjectStream(@NotNull Parcel parcel, @Nullable Externalizable externalizable) {
        writeToObjectStreamInner(parcel, externalizable);
    }

    private static void writeToObjectStreamInner(@NotNull Parcel parcel, @Nullable Object serializable) {
        if (serializable == null) {
            parcel.writeString(null);
            return;
        }

        final String className = serializable.getClass().getName();

        parcel.writeString(className);

        ObjectOutputStream objectOutputStream = null;
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(serializable);
            objectOutputStream.flush();
            parcel.writeByteArray(arrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize " + className, e);
        } finally {
            shut(objectOutputStream);
        }
    }

    /**
     * Internal utility method for aiding code generation.
     */
    @SuppressWarnings("unchecked")
    public static <X> X unsafeCast(Object whatever) {
        return (X) whatever;
    }

    /**
     * Internal utility method for aiding code generation.
     */
    public static void shut(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
                // ok
            }
        }
    }

    /**
     * Internal utility method for aiding code generation.
     */
    public static void verify(IBinder rpc, String interfaceName, long localRpcVersion) throws RemoteException {
        final long interfaceRpcVer;

        Parcel req = Parcel.obtain();
        Parcel resp = Parcel.obtain();
        try {
            req.writeInterfaceToken(interfaceName);
            req.writeLong(localRpcVersion);

            if (!rpc.transact(AidlUtil.VERSION_TRANSACTION, req, resp, 0)) {
                throw new VersionMismatch("Failed to get interface version from remote process");
            }

            resp.readException();

            interfaceRpcVer = resp.readLong();
        } finally {
            req.recycle();
            resp.recycle();
        }

        if (interfaceRpcVer != localRpcVersion) {
            throw new VersionMismatch("RPC interface version mismatch: local is " + localRpcVersion + " remote is " + interfaceRpcVer);
        }
    }
}
