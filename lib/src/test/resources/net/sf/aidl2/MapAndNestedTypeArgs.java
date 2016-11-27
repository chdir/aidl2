package net.sf.aidl2;

import android.graphics.RectF;
import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.List;
import java.util.Map;

@AIDL(assumeFinal = true)
public interface MapAndNestedTypeArgs extends IInterface {
    <T extends List<Parcelable>> void methodWithNestedRecType(Map<RectF, T> weirdMap_) throws RemoteException;
}
