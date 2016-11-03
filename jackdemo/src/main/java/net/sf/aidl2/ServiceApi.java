package net.sf.aidl2;

import android.graphics.RectF;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import net.sf.fakenames.aidl2.demo.Responder;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

@AIDL
public interface ServiceApi extends IInterface {
    String sayHello(Responder responder) throws RemoteException;

    void arrayTest(RectF[] list) throws RemoteException;

    @Override
    IBinder asBinder();
}
