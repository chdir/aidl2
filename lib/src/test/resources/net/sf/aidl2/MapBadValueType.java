package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.Map;

@AIDL(assumeFinal = true)
public interface MapBadValueType extends IInterface {
    Map<String, Runnable> returnMapNoKey() throws RemoteException;
}