package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AIDL(defaultNullable = false)
public interface NullabilityInheritance extends IInterface {
    @Nullable
    Integer testMethod1(Integer p1, @NotNull Integer p2) throws RemoteException;

    Integer testMethod2(Integer p3) throws RemoteException;
}