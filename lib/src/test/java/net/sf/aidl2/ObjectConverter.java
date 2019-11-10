package net.sf.aidl2;

import android.os.Parcel;

import java.lang.reflect.Type;

public class ObjectConverter implements Converter<Object> {
    @Override
    public void write(Object arg, Parcel parcel) {
        parcel.writeValue(arg);
    }

    @Override
    public Object read(Type type, Parcel parcel) {
        return parcel.readValue(getClass().getClassLoader());
    }
}
