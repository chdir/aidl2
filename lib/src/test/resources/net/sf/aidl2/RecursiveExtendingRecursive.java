package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.concurrent.Callable;

@AIDL
public interface RecursiveExtendingRecursive<X extends Callable<X>> extends Callable<RecursiveExtendingRecursive>, Recursive, IInterface {
    @Override
    RecursiveExtendingRecursive call() throws RemoteException;
}
