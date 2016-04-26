package net.sf.aidl2;

import android.os.Binder;
import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.HashSet;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

@AIDL("net.sf.aidl2.ConcreteSetTest")
public interface ConcreteSetTest<X extends Callable & Externalizable, H extends TreeSet<String>> extends IInterface {
    <Y extends Serializable & Parcelable> HashSet<? extends X> methodWithCOWArraySetParamAndHashSetReturn(CopyOnWriteArraySet<Y> objectList) throws RemoteException;

    <J extends ConcurrentSkipListSet<List<Set<Binder>>>> H[] methodWithCSKLSetParamAndTreeSetArray(J concurrentSkipListSet) throws RemoteException;
}
