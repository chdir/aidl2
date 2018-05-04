package net.sf.aidl2;

import android.os.Bundle;
import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.Date;

@AIDL
public interface ComplexInterface extends IInterface {
    byte byteReturn() throws RemoteException;

    short shortReturn() throws RemoteException;

    char charReturn() throws RemoteException;

    int intReturn() throws RemoteException;

    long longReturn() throws RemoteException;

    float floatReturn() throws RemoteException;

    double doubleReturn() throws RemoteException;

    String stringReturn() throws RemoteException;

    Date dateReturn() throws RemoteException;

    Bundle bundleReturn() throws RemoteException;

    Parcelable pReturn() throws RemoteException;

    byte[] bReturn() throws RemoteException;

    char[] cReturn() throws RemoteException;

    float[] fReturn() throws RemoteException;

    String[] sReturn() throws RemoteException;

    void arg(byte arg) throws RemoteException;

    void arg(short shortArg) throws RemoteException;

    void arg(char charArg) throws RemoteException;

    void arg(int intArg) throws RemoteException;

    void arg(long longArg) throws RemoteException;

    void arg(float floatArg) throws RemoteException;

    void arg(double doubleArg) throws RemoteException;

    void arg(String stringArg) throws RemoteException;

    void arg(Date dateArg) throws RemoteException;

    void arg(Bundle bundleArg) throws RemoteException;

    void arg(Parcelable pArg) throws RemoteException;

    void arg(byte[] bArg) throws RemoteException;

    void arg(char[] cArg) throws RemoteException;

    void arg(float[] fArg) throws RemoteException;

    void arg(String[] sArg) throws RemoteException;
}
