package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;

@AIDL("net.sf.aidl2.ConcreteSetTypeArgsAndTypeLoss")
public interface ConcreteSetTypeArgsAndTypeLoss<X extends Callable & Externalizable> extends IInterface {
    <Y extends Serializable & Parcelable > HashSet<? extends X> methodWithCOWArraySetParamAndHashSetReturn(CopyOnWriteArraySet<Y> objectList) throws RemoteException;
}
