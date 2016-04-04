package net.sf.fakenames.aidl2;

import android.os.IBinder;
import android.os.IInterface;

public interface Linker {
    <X extends IInterface> IBinder newServer(X server, Class<X> type);

    <X extends IInterface> X newClient(IBinder binder, Class<X> type);
}
