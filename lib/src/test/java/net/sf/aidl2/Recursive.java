package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.concurrent.Callable;

@AIDL
public interface Recursive<UU extends Callable<UU>> extends IInterface {
    Parcelable someMethod() throws RemoteException;
}
