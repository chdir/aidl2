// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Boolean;
import java.lang.Deprecated;
import java.lang.Override;

/**
 * Perform IPC calls according to the interface contract.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class BooleanArrayTest$$AidlClientImpl<B extends Boolean> implements BooleanArrayTest<B> {
    private final IBinder delegate;

    public BooleanArrayTest$$AidlClientImpl(IBinder delegate) throws RemoteException {
        this.delegate = delegate;
    }

    @Override
    public IBinder asBinder() {
        return delegate;
    }

    @Override
    public void methodWithBiCharArrayReturn(B[][][] booleanArrayParam) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(BooleanArrayTest$$AidlServerImpl.DESCRIPTOR);

            if (booleanArrayParam == null) {
                data.writeInt(-1);
            } else {
                data.writeInt(booleanArrayParam.length);
                for (Boolean[][] booleanArrayParamComponent : booleanArrayParam) {
                    if (booleanArrayParamComponent == null) {
                        data.writeInt(-1);
                    } else {
                        data.writeInt(booleanArrayParamComponent.length);
                        for (Boolean[] booleanArrayParamComponent_ : booleanArrayParamComponent) {
                            if (booleanArrayParamComponent_ == null) {
                                data.writeInt(-1);
                            } else {
                                data.writeInt(booleanArrayParamComponent_.length);
                                for (Boolean booleanArrayParamComponent__ : booleanArrayParamComponent_) {
                                    if (booleanArrayParamComponent__ == null) {
                                        data.writeByte((byte) -1);
                                    } else {
                                        data.writeByte((byte) 0);
                                        data.writeInt(booleanArrayParamComponent__ ? 1 : 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            delegate.transact(BooleanArrayTest$$AidlServerImpl.TRANSACT_methodWithBiCharArrayReturn, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
