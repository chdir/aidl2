package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;

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
}
