package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.SizeF;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@AIDL
public interface SimpleMap extends IInterface {
    Map<String, Integer> abstractMapMethod(Map<Parcelable, SizeF> responder) throws RemoteException;

    LinkedHashMap<String, Integer> concreteMapMethod(HashMap<Parcelable, SizeF> responder) throws RemoteException;
}
