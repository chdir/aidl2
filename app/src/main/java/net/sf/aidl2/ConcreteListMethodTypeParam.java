package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.Stack;
import java.util.Vector;

@AIDL("net.sf.aidl2.ConcreteListMethodTypeParam")
public interface ConcreteListMethodTypeParam extends IInterface {
    <T extends CharSequence> Stack<T> methodWithVectorParamAndStackReturn(Vector<Parcelable> objectList) throws RemoteException;
}
