package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.concurrent.Callable;

@AIDL
public interface ListAndNestedTypeArgs extends IInterface {
    <T extends Callable<T>, U extends ArrayList<Recursive<T>>> U methodWithNestedRecType(U nastyList) throws RemoteException;
}
