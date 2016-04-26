package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

@AIDL
public interface ConcreteListTest<X extends LinkedList<Object>> extends IInterface {
    @SuppressWarnings("unchecked")
    ArrayList<? super String> methodWithLinkedListParamAndArrayListReturn(X objectList) throws RemoteException;

    <T extends CharSequence> Stack<T> methodWithVectorParamAndStackReturn(Vector<Parcelable> objectList) throws RemoteException;
}
