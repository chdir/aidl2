package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.LinkedList;

@SuppressWarnings("unchecked")
@AIDL("net.sf.aidl2.ConcreteListClassTypeParamAndRaw")
public interface ConcreteListClassTypeParamAndRaw<X extends LinkedList<Object>> extends IInterface {
    ArrayList<? super String> methodWithLinkedListParamAndArrayListReturn(X objectList) throws RemoteException;
}
