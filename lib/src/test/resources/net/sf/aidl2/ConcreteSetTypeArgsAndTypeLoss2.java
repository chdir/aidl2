package net.sf.aidl2;

import android.os.Binder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

@AIDL("net.sf.aidl2.ConcreteSetTypeArgsAndTypeLoss2")
public interface ConcreteSetTypeArgsAndTypeLoss2<H extends TreeSet<String>> extends IInterface {
    <J extends ConcurrentSkipListSet<List<Set<Binder>>>> H[] methodWithCSKLSetParamAndTreeSetArray(J concurrentSkipListSet) throws RemoteException;
}
