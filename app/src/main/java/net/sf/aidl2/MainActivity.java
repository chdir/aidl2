package net.sf.aidl2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.sf.fakenames.gprocessor.R;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }
}
