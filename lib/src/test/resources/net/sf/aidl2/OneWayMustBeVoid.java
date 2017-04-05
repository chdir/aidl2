package net.sf.aidl2;

import android.os.IInterface;

@AIDL
public interface OneWayMustBeVoid extends IInterface {
    @OneWay
    String nonVoidOneWay();
}
