package net.sf.aidl2;

import android.os.Parcel;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;

public class AidlUtil {
    @SuppressWarnings("unchecked")
    public static <X extends Serializable> X readSafeSerializable(Parcel parcel) {
        return (X) parcel.readSerializable();
    }

    @SuppressWarnings("unchecked")
    public static <X extends Externalizable> X readSafeExternalizable(Parcel parcel) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <X> X unsafeCast(Object whatever) {
        return (X) whatever;
    }

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
