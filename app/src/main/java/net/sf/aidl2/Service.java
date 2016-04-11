package net.sf.aidl2;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class Service extends android.app.Service {
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("BOUND", "!!!");

        return InterfaceLoader.asBinder(new ServiceApi() {
            @Override
            public String test() throws RemoteException {
                return "Hello world";
            }

            @Override
            public IBinder asBinder() {
                throw new UnsupportedOperationException();
            }
        }, ServiceApi.class);
    }
}
