package net.sf.aidl2;

import android.os.Parcel;
import android.os.Parcelable;

public final class ParametrizedParcelable<T extends Number> implements Parcelable {
    private final String nameOfCat;

    private final T catsChildren;

    public ParametrizedParcelable(String nameOfCat, T catsChildren) {
        this.nameOfCat = nameOfCat;
        this.catsChildren = catsChildren;
    }

    @SuppressWarnings("unchecked")
    protected ParametrizedParcelable(Parcel in) {
        nameOfCat = in.readString();
        catsChildren = (T) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameOfCat);
        dest.writeSerializable(catsChildren);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParametrizedParcelable> CREATOR = new Creator<ParametrizedParcelable>() {
        @Override
        public ParametrizedParcelable createFromParcel(Parcel in) {
            return new ParametrizedParcelable(in);
        }

        @Override
        public ParametrizedParcelable[] newArray(int size) {
            return new ParametrizedParcelable[size];
        }
    };
}
