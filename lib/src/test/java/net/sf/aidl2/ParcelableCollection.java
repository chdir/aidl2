package net.sf.aidl2;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.AbstractList;
import java.util.Arrays;

public class ParcelableCollection extends AbstractList<Integer> implements Parcelable {
    private final int[] ints;

    public ParcelableCollection() {
        this(0);
    }

    public ParcelableCollection(int size) {
        this.ints = new int[size];
    }

    @Override
    public Integer get(int i) {
        return ints[i];
    }

    @Override
    public boolean add(Integer integer) {
        Arrays.copyOf(ints, ints.length + 1);

        ints[ints.length - 1] = integer;

        return true;
    }

    @Override
    public int size() {
        return ints.length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(ints);
    }

    public static final Creator<ParcelableCollection> CREATOR = new Creator<ParcelableCollection>() {
        @Override
        public ParcelableCollection createFromParcel(Parcel source) {
            int size = source.readInt();
            ParcelableCollection c = new ParcelableCollection(size);
            source.readIntArray(c.ints);
            return c;
        }

        @Override
        public ParcelableCollection[] newArray(int size) {
            return new ParcelableCollection[size];
        }
    };
}