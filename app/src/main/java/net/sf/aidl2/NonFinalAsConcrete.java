package net.sf.aidl2;

import android.graphics.RectF;
import android.os.IInterface;
import android.os.RemoteException;
import org.jetbrains.annotations.NotNull;

@AIDL(assumeFinal = true)
public interface NonFinalAsConcrete extends IInterface {
    RectF testMethod(RectF nonFinalParcelableParam) throws RemoteException;

    @NotNull
    RectF allNotNullTestMethod(@NotNull RectF nonFinalParcelableParam) throws RemoteException;

    SomeNonFinalExternalizable exMethod(SomeNonFinalExternalizable nonFinalExParam) throws RemoteException;

    @NotNull
    SomeNonFinalExternalizable notNullExMethod(@NotNull SomeNonFinalExternalizable nonFinalExParam) throws RemoteException;
}
