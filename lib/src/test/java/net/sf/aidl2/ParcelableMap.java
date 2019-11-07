package net.sf.aidl2;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class ParcelableMap extends AbstractMap<Integer, String> implements Parcelable {
    private final SparseArray<String> container;

    public ParcelableMap() {
        this(new SparseArray<>());
    }

    protected ParcelableMap(SparseArray<String> container) {
        this.container = container;
    }

    @Override
    public int size() {
        return container.size();
    }

    @Override
    @NotNull
    public Set<Entry<Integer, String>> entrySet() {
        return new AbstractSet<Entry<Integer, String>>() {
            @Override
            @NotNull
            public Iterator<Entry<Integer, String>> iterator() {
                return new Iterator<Entry<Integer, String>>() {
                    int idx;

                    @Override
                    public boolean hasNext() {
                        return idx != container.size() - 1;
                    }

                    @Override
                    public Entry<Integer, String> next() {
                        Entry<Integer, String> e = new SimpleImmutableEntry<>(container.keyAt(idx), container.valueAt(idx));

                        ++idx;

                        return e;
                    }
                };
            }

            @Override
            public int size() {
                return container.size();
            }
        };
    }

    @Override
    public String put(Integer aLong, String s) {
        int idx = container.indexOfKey(aLong);
        if (idx < 0) {
            container.put(aLong, s);

            return null;
        } else {
            String old = container.valueAt(idx);

            container.setValueAt(idx, s);

            return old;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSparseArray((SparseArray) container);
    }

    public static final Creator<ParcelableMap> CREATOR = new Creator<ParcelableMap>() {
        @Override
        @SuppressWarnings("unchecked")
        public ParcelableMap createFromParcel(Parcel source) {
            SparseArray<String> map = (SparseArray<String>) source.readSparseArray(ParcelableMap.class.getClassLoader());

            return new ParcelableMap(map);
        }

        @Override
        public ParcelableMap[] newArray(int size) {
            return new ParcelableMap[size];
        }
    };
}