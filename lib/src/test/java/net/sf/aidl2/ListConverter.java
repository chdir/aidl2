package net.sf.aidl2;

import android.os.Parcel;

import java.lang.reflect.Type;
import java.util.List;

public class ListConverter implements Converter<List<String>> {
    @Override
    public void write(List<String> arg, Parcel parcel) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public List<String> read(Type type, Parcel parcel) {
        throw new UnsupportedOperationException("todo");
    }
}
