package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import net.sf.fakenames.aidl2.demo.Responder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Config(manifest = "build/intermediates/manifests/full/debug/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class SimpleTransmissionTest {
    @Test
    public void attachLocalWorks() throws Exception {
        ServiceApi impl = new ServiceApi() {
            @Override
            public String sayHello(Responder responder) throws RemoteException {
                return "hello";
            }

            @Override
            public IBinder asBinder() {
                return InterfaceLoader.asBinder(this, ServiceApi.class);
            }
        };

        ServiceApi proxy = InterfaceLoader.asInterface(impl.asBinder(), ServiceApi.class);

        assertTrue(proxy == impl);
    }

    @Test
    public void detachLocalWorks() throws Exception {
        ServiceApi impl = new ServiceApi() {
            @Override
            public String sayHello(Responder responder) throws RemoteException {
                return "hello";
            }

            @Override
            public IBinder asBinder() {
                Binder binder = (Binder) InterfaceLoader.asBinder(this, ServiceApi.class);

                binder.attachInterface(null, binder.getInterfaceDescriptor());

                return binder;
            }
        };

        ServiceApi proxy = InterfaceLoader.asInterface(impl.asBinder(), ServiceApi.class);

        assertTrue(proxy != impl);
    }

    @Test
    public void intArgWorks() throws Exception {
        IntArg impl = new IntArg() {
            @Override
            public void methodWithIntParameter(int parameter) throws RemoteException {
                assertEquals(parameter, 42);
            }

            @Override
            public IBinder asBinder() {
                Binder binder = (Binder) InterfaceLoader.asBinder(this, IntArg.class);

                binder.attachInterface(null, binder.getInterfaceDescriptor());

                return binder;
            }
        };

        IntArg proxy = InterfaceLoader.asInterface(impl.asBinder(), IntArg.class);

        proxy.methodWithIntParameter(42);
    }
}
