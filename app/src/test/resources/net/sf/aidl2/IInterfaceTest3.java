package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import net.sf.fakenames.aidl2.demo.Responder;

@AIDL("net.sf.aidl2.IInterfaceTest3")
public interface IInterfaceTest3 extends IInterface {
    Responder methodWithOldAidlCallbackReturn() throws RemoteException;
}