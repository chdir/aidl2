package net.sf.fakenames.aidl2;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.system.Os;

import net.sf.fakenames.MyAidl;

import java.io.FileDescriptor;

/**
 * Created by uniqa on 3/6/16.
 */
public class Test implements Wow {
    {
        Parcel g = null;
        MyAidl.Stub.asInterface(null);
        g.writeFileDescriptor(null);
        ParcelFileDescriptor gg = null;
        FileDescriptor ggg = gg.getFileDescriptor();
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                FileDescriptor gggg = Os.dup(ggg);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        TheAIDL2 wer = null;

        //wer.cool2();
    }

    @Override
    public Number cool(Number wow) {
        return null;
    }
}
