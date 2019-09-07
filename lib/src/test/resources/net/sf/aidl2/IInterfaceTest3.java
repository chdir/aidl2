package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import org.jetbrains.annotations.NotNull;

@AIDL("net.sf.aidl2.IInterfaceTest3")
public interface IInterfaceTest3 extends IInterface {
    @NotNull
    RemoteApi methodWithCallbackReturn() throws RemoteException;
}
