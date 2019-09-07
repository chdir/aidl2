package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AIDL(assumeFinal = true)
public interface MapSerializable<T extends HashMap<Integer, Serializable>> extends IInterface {
    HashMap<String, Date> maps(T arg) throws RemoteException;
}
