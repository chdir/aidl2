package net.sf.aidl2;

import android.os.Binder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.Collection;
import java.util.List;

@AIDL("net.sf.aidl2.AbstractListSimpleTypeMismatch")
public interface AbstractListSimpleTypeMismatch extends IInterface {
    List<Binder> methodWithListReturnAndCollectionParam(Collection<Integer> ints) throws RemoteException;
}
