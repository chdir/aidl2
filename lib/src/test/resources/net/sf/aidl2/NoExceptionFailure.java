package net.sf.aidl2;

import android.os.IInterface;

@AIDL("net.sf.aidl2.NoExceptionFailure")
public interface NoExceptionFailure extends IInterface {
    void someMethod();
}
