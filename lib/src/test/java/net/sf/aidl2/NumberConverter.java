package net.sf.aidl2;

import android.os.Parcel;

import java.lang.reflect.Type;

public class NumberConverter implements Converter<Number> {
    @Override
    public void write(Number arg, Parcel parcel) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public Number read(Type type, Parcel parcel) {
        throw new UnsupportedOperationException("todo");
    }
}
