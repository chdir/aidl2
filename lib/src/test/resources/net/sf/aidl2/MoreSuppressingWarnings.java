package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;
import java.util.Map;

@AIDL
public interface MoreSuppressingWarnings extends IInterface {
    @SuppressWarnings({"wuhahahaha", "chirpkippy"})
    void aMethod() throws IllegalArgumentException, RemoteException;

    @SuppressWarnings({"foobar", "all"})
    Object objectReturn() throws RemoteException;

    @SuppressWarnings("all")
    List rawTypes(Map rawSet) throws RemoteException;
}
