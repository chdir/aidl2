package net.sf.fakenames.aidl2;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.sf.aidl2.InterfaceLoader;
import net.sf.aidl2.ServiceApi;
import net.sf.aidl2.Service;
import net.sf.fakenames.aidl2.demo.Responder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ServiceTest {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Test
    public void testServiceInteraction() throws TimeoutException, RemoteException {
        Intent serviceIntent = new Intent(InstrumentationRegistry.getTargetContext(), Service.class);

        IBinder binder = mServiceRule.bindService(serviceIntent);

        ServiceApi api = InterfaceLoader.asInterface(binder, ServiceApi.class);

        final String whatever = UUID.randomUUID().toString();

        assertThat(api.sayHello(new Responder.Stub() {
            @Override
            public String sayHello() throws RemoteException {
                return whatever;
            }
        })).isEqualTo(whatever);
    }
}