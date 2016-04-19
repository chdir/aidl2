package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.Date;
import java.util.concurrent.Callable;

@AIDL(value = "net.sf.aidl2.ExtendingInterface")
public interface ExtendingInterface extends Callable<Date>, IInterface {
    @Override
    Date call() throws RemoteException, IllegalArgumentException;
}
