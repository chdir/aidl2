package net.sf.aidl2;

import android.os.Parcel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Interface for type converters.
 *
 * Implementations are expected to be re-usable and fully thread-safe.
 */
public interface Converter<T> {
    /**
     * Serialize AIDL method argument to Parcel.
     *
     * Do not hold onto Parcel reference after this method returns.
     *
     * @param arg value to serialize
     * @param parcel Parcel that shall contain the value
     */
    void write(T arg, Parcel parcel);

    /**
     * Read value from Parcel
     *
     * @param type the actual type of value (may be {@link ParameterizedType} or other {@link Type} subclass)
     * @param parcel Parcel to read the value from
     *
     * @return the value, conforming to requested type
     */
    T read(Type type, Parcel parcel);
}
