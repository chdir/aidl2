package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface RecursiveType extends IInterface {
    <T extends Recursive<? extends T>> void wow(T trouble) throws RemoteException;
}
