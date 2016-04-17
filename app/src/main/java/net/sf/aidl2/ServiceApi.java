package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import net.sf.fakenames.aidl2.demo.Responder;

@AIDL
public interface ServiceApi extends IInterface {
    String sayHello(Responder responder) throws RemoteException;
}
