package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

@AIDL
public interface MethodAndParamNameIsolation1 extends IInterface {
    void bigDeal(String theStringVariable) throws RemoteException;

    void bigDeal(String theStringVariable, String anotherString) throws RemoteException;

    void bigDeal(String theStringVariable, String anotherString, cunningType cunningType) throws RemoteException;

    void cunningType(String theStringVariable, String anotherString, cunningType cunningType, MethodAndParamNameIsolation2 MethodAndParamNameIsolation2) throws RemoteException;

    void cunningType(String theStringVariable, String anotherString, cunningType cunningType, MethodAndParamNameIsolation2 MethodAndParamNameIsolation2, RemoteException... RemoteException) throws RemoteException;
}
