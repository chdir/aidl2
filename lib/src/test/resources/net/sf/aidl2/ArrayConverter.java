package net.sf.aidl2;

import android.os.Parcel;

import java.lang.reflect.Type;

public class ArrayConverter implements Converter<String[][]> {
    @Override
    public void write(String[][] arg, Parcel parcel) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public String[][] read(Type type, Parcel parcel) {
        throw new UnsupportedOperationException("todo");
    }
}
