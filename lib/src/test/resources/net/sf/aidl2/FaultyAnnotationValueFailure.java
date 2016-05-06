package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import com.example.NonExistingType;

@AIDL(NonExistingType.NonExistingAnnotationParameter)
public interface FaultyAnnotationValueFailure extends IInterface {
    void foobar() throws RemoteException;
}