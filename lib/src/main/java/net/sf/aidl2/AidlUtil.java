package net.sf.aidl2;

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
    /**
     * Read Serializable object from Parcel without making assumptions about it's runtime type.
     */
    @SuppressWarnings("unchecked")
    public static @Nullable <X extends Serializable> X readSafeSerializable(@NotNull Parcel parcel) {
        return (X) parcel.readSerializable();
    }

    /**
     * Read Externalizable object from Parcel without making assumptions about it's runtime type.
     */
    @SuppressWarnings("unchecked")
    public static @Nullable<X extends Externalizable> X readSafeExternalizable(@NotNull Parcel parcel) {
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
     * Write Externalizable object to Parcel without making assumptions about it's runtime type.
     */
    public static void writeExternalizable(@NotNull Parcel parcel, @Nullable Externalizable externalizable) {
        if (externalizable == null) {
            parcel.writeString(null);
            return;
        }

        final String className = externalizable.getClass().getName();

        parcel.writeString(className);

        ObjectOutputStream objectOutputStream = null;
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(externalizable);
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
