package net.sf.aidl2;

import android.os.Parcel;
import android.os.Parcelable;

public class SomeParcelable implements Parcelable, Runnable {
    private final String nameOfCat;

    private volatile int catsChildren;

    public SomeParcelable(String nameOfCat, int catsChildren) {
        this.nameOfCat = nameOfCat;
        this.catsChildren = catsChildren;
    }

    protected SomeParcelable(Parcel in) {
        nameOfCat = in.readString();
        catsChildren = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameOfCat);
        dest.writeInt(catsChildren);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SomeParcelable> CREATOR = new Creator<SomeParcelable>() {
        @Override
        public SomeParcelable createFromParcel(Parcel in) {
            return new SomeParcelable(in);
        }

        @Override
        public SomeParcelable[] newArray(int size) {
            return new SomeParcelable[size];
        }
    };

    @Override
    public void run() {
        ++catsChildren;
    }
}
