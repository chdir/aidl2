// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.lang.Boolean;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class BooleanArrayTest$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.BooleanArrayTest";

    static final int TRANSACT_methodWithBiCharArrayReturn = IBinder.FIRST_CALL_TRANSACTION;

    private final BooleanArrayTest<?> delegate;

    public BooleanArrayTest$$AidlServerImpl(BooleanArrayTest<?> delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch(code) {
            case TRANSACT_methodWithBiCharArrayReturn: {
                data.enforceInterface(this.getInterfaceDescriptor());

                final Boolean[][][] booleanArrayParamArray;
                final int booleanArrayParamLength = data.readInt();
                if (booleanArrayParamLength < 0) {
                    booleanArrayParamArray = null;
                } else {
                    booleanArrayParamArray = new Boolean[booleanArrayParamLength][][];
                    for (int i = 0; i < booleanArrayParamArray.length; i++) {
                        final Boolean[][] booleanArrayParamArray_;
                        final int booleanArrayParamLength_ = data.readInt();
                        if (booleanArrayParamLength_ < 0) {
                            booleanArrayParamArray_ = null;
                        } else {
                            booleanArrayParamArray_ = new Boolean[booleanArrayParamLength_][];
                            for (int i_ = 0; i_ < booleanArrayParamArray_.length; i_++) {
                                final Boolean[] booleanArrayParamArray__;
                                final int booleanArrayParamLength__ = data.readInt();
                                if (booleanArrayParamLength__ < 0) {
                                    booleanArrayParamArray__ = null;
                                } else {
                                    booleanArrayParamArray__ = new Boolean[booleanArrayParamLength__];
                                    for (int i__ = 0; i__ < booleanArrayParamArray__.length; i__++) {
                                        final Boolean booleanArrayParamTmp;
                                        if (data.readByte() == -1) {
                                            booleanArrayParamTmp = null;
                                        } else {
                                            booleanArrayParamTmp = data.readInt() == 1;
                                        }
                                        booleanArrayParamArray__[i__] = booleanArrayParamTmp;
                                    }
                                }
                                booleanArrayParamArray_[i_] = booleanArrayParamArray__;
                            }
                        }
                        booleanArrayParamArray[i] = booleanArrayParamArray_;
                    }
                }

                delegate.methodWithBiCharArrayReturn(AidlUtil.unsafeCast(booleanArrayParamArray));
                reply.writeNoException();

                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
