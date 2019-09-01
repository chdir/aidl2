package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.Map;

@AIDL(assumeFinal = true)
public interface MapBadKeyType extends IInterface {
    Map<Runnable, String> returnMapNoKey() throws RemoteException;
}