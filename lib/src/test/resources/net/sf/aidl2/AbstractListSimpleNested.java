package net.sf.aidl2;

import android.os.IInterface;
import android.os.RemoteException;
import android.util.SizeF;

import java.util.AbstractList;
import java.util.Date;
import java.util.List;

@AIDL("net.sf.aidl2.AbstractListSimpleNested")
public interface AbstractListSimpleNested extends IInterface {
    AbstractList<List<Date>> methodWithAbstractListReturn(List<SizeF[][]> sizes) throws RemoteException;
}
