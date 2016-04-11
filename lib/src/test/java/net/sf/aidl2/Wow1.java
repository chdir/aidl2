package net.sf.aidl2;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class Wow1<PPP, E extends Wow<PPP>> implements Wow, Parcelable {
    protected Wow1(Parcel in) {
    }

    public <T extends Parcelable & Cloneable> void hah(T yesss) {

    }

    public static final Creator CREATOR = new Creator<Parcelable>() {
        @Override
        public Wow1 createFromParcel(Parcel in) {
            return new Wow1(in);
        }

        @Override
        public Wow1[] newArray(int size) {
            return new Wow1[size];
        }
    };

    @Override
    public Integer cool(Number wow) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // ok
    }

    static {
        Creator<? super Parcelable> p = CREATOR;
        ObjectInputStream ghj;
        ByteArrayInputStream ghi;
    }
}
