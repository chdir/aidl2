package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;
import android.util.SizeF;

import java.util.AbstractList;
import java.util.Collection;

@AIDL("net.sf.aidl2.AbstractListTest")
public interface AbstractListTest extends IInterface {
    Collection<AbstractListTest> methodWithCollectionReturn(AbstractList<SizeF> abstrList) throws RemoteException;
}
