package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

@AIDL
public interface WildcardBoundByTypeVar extends IInterface {
    <T extends Parcelable, U extends Parametrized<? extends T>> U wow(U trouble) throws RemoteException;
}