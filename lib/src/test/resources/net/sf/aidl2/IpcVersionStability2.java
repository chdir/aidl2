package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@AIDL("net.sf.aidl2.IpcVersionStability")
public interface IpcVersionStability2 extends IInterface {
    String sayHello() throws RemoteException;

    List<String> test(Set<String> alsoTest) throws RemoteException;

    void method() throws RemoteException;

    @NotNull Integer method(int intParam) throws RemoteException;

    <T extends Date> void alsoMapTestButDifferentlyNamed(LinkedHashMap<Parcelable, T> mapArg) throws RemoteException;

    @Call(1)
    char test(long i) throws RemoteException;
}
