package net.sf.aidl2;

import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.SizeF;

import java.io.Serializable;
import java.util.List;

@AIDL
public interface AllTogether<YY extends Serializable & Cloneable, T extends Number> extends IInterface {
    int wow(byte one,
            long[] two,
            Character[] three,
            Integer[][] four,
            String[][][] five) throws RemoteException;

    <P extends Cloneable & Parcelable> void cool(P[][] object) throws RemoteException;

    Integer cool2(YY[] object) throws RemoteException;

    <X extends YY> X hazard() throws RemoteException;

    <GGG extends Cloneable & Serializable> GGG wut(Wow1<Wow, Wow<Wow>> some, Somesome somesome, String str, SizeF badBoy) throws RemoteException;

    @net.sf.aidl2.OneWay
    Void wut2(Wow1 some, Somesome<Somesome.Ohreally> somesome) throws RemoteException;

    <Z> void ok(@SuppressWarnings("aidl2") Z[] input) throws RemoteException;

    <F extends List<? extends String> & Parcelable> void test2(F cool) throws RemoteException;

    void methodWithParcelableParam(ParametrizedParcelable<? super T> parcelable) throws RemoteException;
}
