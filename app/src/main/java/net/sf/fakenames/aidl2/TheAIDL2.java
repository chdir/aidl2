package net.sf.fakenames.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.SizeF;

import net.sf.aidl2.AIDL;

import java.io.Serializable;
import java.util.List;

@AIDL
public interface TheAIDL2<YY extends Serializable & Cloneable> extends IInterface {
    int wow(byte one,
            long[] two,
            Character[] three,
            Integer[][] four,
            String[][][] five) throws RemoteException;

    <T extends Cloneable & Parcelable> void cool(T[][] object) throws RemoteException;

    Integer cool2(YY[] object) throws RemoteException;

    <X extends YY> X hazard() throws RemoteException;

    <GGG extends Cloneable & Serializable> GGG wut(Wow1<Wow, Wow<Wow>> some, Somesome somesome, String str, SizeF badBoy) throws RemoteException;

    @net.sf.aidl2.OneWay
    Void wut2(Wow1 some, Somesome<Somesome.Ohreally> somesome) throws RemoteException;

    <T> void ok(@SuppressWarnings("aidl2") T[] input) throws RemoteException;

    <F extends List<? extends String> & Parcelable> void test2(F cool) throws RemoteException;
}
