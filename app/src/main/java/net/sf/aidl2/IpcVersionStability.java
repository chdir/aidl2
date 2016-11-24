package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Map;

@AIDL("net.sf.aidl2.IpcVersionStability")
public interface IpcVersionStability extends IInterface {
    char test(long i) throws RemoteException;

    String sayHello() throws RemoteException;

    String[] test(String[] alsoTest) throws RemoteException;

    Void method(Void nothing) throws RemoteException;

    int method(@NotNull Integer intParam) throws RemoteException;

    void mapTest(Map<? extends Parcelable, Serializable> m) throws RemoteException;
}
