package net.sf.aidl2;

import android.content.Intent;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.RemoteException;

import net.sf.fakenames.aidl2.demo.Responder;

import java.util.List;

public class Service extends android.app.Service {
    @Override
    public IBinder onBind(Intent intent) {
        final ServiceApi remoteApi = new ServiceApi() {
            @Override
            public String sayHello(Responder responder) throws RemoteException {
                return responder.sayHello();
            }

            @Override
            public void arrayTest(RectF[] list) throws RemoteException {

            }

            @Override
            public IBinder asBinder() {
                return InterfaceLoader.asBinder(this, ServiceApi.class);
            }
        };

        return remoteApi.asBinder();
    }
}
