package net.sf.aidl2;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public class StreamConverter implements Converter<FileOutputStream> {
    @Override
    public void write(FileOutputStream arg, Parcel parcel) {
        try {
            ParcelFileDescriptor pfd = ParcelFileDescriptor.dup(arg.getFD());

            pfd.writeToParcel(parcel, Parcelable.CONTENTS_FILE_DESCRIPTOR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public FileOutputStream read(Type type, Parcel parcel) {
        ParcelFileDescriptor pfd = ParcelFileDescriptor.CREATOR.createFromParcel(parcel);

        return new FileOutputStream(pfd.getFileDescriptor()) {
            @Override
            public void close() throws IOException {
                super.close();

                pfd.close();
            }
        };
    }
}
