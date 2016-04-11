package net.sf.aidl2;

import android.app.Service;
import android.os.Binder;
import android.os.Parcel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface for processing by AIDL2 annotation processor. Putting this annotation on
 * non-interfaces will result in compilation error.
 *
 * The annotation processor will generate an implementation of parameter-marshalling code, using
 * {@link Binder} and {@link Parcel} Android classes. Those implementations can be successively
 * accessed via {@link InterfaceLoader}.
 *
 * The processor generates implementation code for every abstract abstrMethod, present in interface,
 * including methods, inherited from parent interfaces. Default methods and methods, inherited from
 * {@link Object}, are ignored — you can force them to be included by directly declaring them in
 * interface code.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AIDL {
    /**
     * String descriptor of interface, advertised to client processes.
     *
     * Default is fully qualified name of interface class.
     */
    String value() default "";

    /**
     * Marking the annotated interface as not security-sensitive allows generation of simpler,
     * more efficient code in situations, when complete security of Binder communications
     * can be assured.
     *
     * You may set this to true, when following conditions are met:
     *
     * 1) The server does not expose itself to untrusted clients (e.g. the {@link Service} is
     * not exported or protected from other applications by signature-level permission).
     * 2) Clients never leak received {@link Binder} to untrusted processes.
     *
     * A good example is an application, internally split in several processes, all of which
     * execute only trusted code. In contrast, do not set this to false, when you expect third party
     * code (such as plugins) to communicate with yours via the annotated interface.
     *
     * Default is false.
     */
    boolean insecure() default false;

    /**
     * Whether to consider abstrMethod parameters and return values nullable by default.
     *
     * Setting this to false will omit some null checks from generated code and may result
     * in NullPointerExceptions within generated code, if the contract is violated.
     *
     * Default is true.
     */
    boolean defaultNullable() default true;
}
