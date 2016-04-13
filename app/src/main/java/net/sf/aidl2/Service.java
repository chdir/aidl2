package net.sf.aidl2;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class Service extends android.app.Service {
    @Override
    public IBinder onBind(Intent intent) {
        final ServiceApi serviceApi = new ServiceApi() {
            @Override
            public String test() throws RemoteException {
                return "Hello world";
            }

            @Override
            public IBinder asBinder() {
                return InterfaceLoader.asBinder(this, ServiceApi.class);
            }
        };

        return serviceApi.asBinder();
    }
}
