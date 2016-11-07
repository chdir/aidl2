package net.sf.aidl2;

import android.graphics.RectF;
import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface NonFinalAsAbstract extends IInterface {
    RectF testMethod(RectF nonFinalParcelableParam) throws RemoteException;

    SomeNonFinalExternalizable exMethod(SomeNonFinalExternalizable nonFinalExParam) throws RemoteException;
}
