package net.sf.aidl2;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleParcelable implements Parcelable {
    private final String nameOfCat;

    private final int catsChildren;

    public SimpleParcelable(String nameOfCat, int catsChildren) {
        this.nameOfCat = nameOfCat;
        this.catsChildren = catsChildren;
    }

    protected SimpleParcelable(Parcel in) {
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

    public static final Creator<SimpleParcelable> CREATOR = new Creator<SimpleParcelable>() {
        @Override
        public SimpleParcelable createFromParcel(Parcel in) {
            return new SimpleParcelable(in);
        }

        @Override
        public SimpleParcelable[] newArray(int size) {
            return new SimpleParcelable[size];
        }
    };
}
