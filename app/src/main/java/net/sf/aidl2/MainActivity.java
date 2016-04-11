package net.sf.aidl2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import net.sf.fakenames.gprocessor.BuildConfig;
import net.sf.fakenames.gprocessor.R;

public class MainActivity extends Activity implements ServiceConnection {
    private static final Intent bindingIntent = new Intent()
            .setClassName(BuildConfig.APPLICATION_ID, Service.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(bindingIntent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        unbindService(this);

        super.onStop();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        try {
            final ServiceApi serviceApi = InterfaceLoader.asInterface(service, ServiceApi.class);

            final String callResult = serviceApi.test();

            Toast.makeText(this, "Received message \"" + callResult + '"', Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            Toast.makeText(this, "Failed to receive a string " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}
