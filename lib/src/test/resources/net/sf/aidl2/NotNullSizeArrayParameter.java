package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;
import android.util.SizeF;

import org.jetbrains.annotations.NotNull;

@AIDL("net.sf.aidl2.NotNullSizeArrayParameter")
public interface NotNullSizeArrayParameter extends IInterface {
    void methodWithNotNullSizeArrayParameter(@NotNull SizeF[] sizeArrayParam) throws RemoteException;
}
