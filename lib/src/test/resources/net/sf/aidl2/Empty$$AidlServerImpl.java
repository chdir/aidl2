// AUTO-GENERATED FILE.  DO NOT MODIFY.
package net.sf.aidl2;

import android.os.Binder;
import java.lang.Deprecated;
import java.lang.String;

/**
 * Handle incoming IPC calls by forwarding them to provided delegate.
 *
 * You can create instances of this class, using {@link InterfaceLoader}.
 *
 * @deprecated — do not use this class directly in your Java code (see above)
 */
@Deprecated
public final class Empty$$AidlServerImpl extends Binder {
    static final String DESCRIPTOR = "net.sf.aidl2.Empty";

    private final Empty delegate;

    public Empty$$AidlServerImpl(Empty delegate) {
        this.delegate = delegate;

        this.attachInterface(delegate, DESCRIPTOR);
    }
}
