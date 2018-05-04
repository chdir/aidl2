package net.sf.aidl2;

import android.app.Service;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Externalizable;
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
 * The processor generates implementation code for every abstract method, present in interface,
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
     * When {@link #insecure} is set to false (default value), this string is transferred between
     * server and client with every Binder transaction.
     *
     * Default is fully qualified name of interface class.
     */
    String value() default "";

    /**
     * Treat all concrete classes as effectively final.
     *
     * When AIDL2 generates code, it assumes, that all non-final concrete types in method signatures may be represented
     * by different subclass at runtime. E.g. a method, that accepts {@code RectF} as parameter, may be called
     * with {@code UserRectF} (subclassing {@code RectF} and possibly making incompatible changes to serialized data).
     * Because of this, a handful of reflection has to be used. Setting this parameter to true will cause AIDL2 to
     * directly emit references to {@code CREATOR} field of {@link Parcelable} classes and directly call constructors
     * of {@link Externalizable} types, no matter final or not.
     *
     * Note, that types, serialized by container convention (collections and maps), are always treated as effectively
     * final and instantiated based on compile-time type alone.
     *
     * Default is false.
     */
    boolean assumeFinal() default false;

    /**
     * Marking the annotated interface as not security-sensitive allows generation of simpler,
     * more efficient code in situations, when complete security of Binder communications
     * has been assured.
     *
     * You may set this to true, when following conditions are met:
     *
     * 1) The server does not expose itself to untrusted clients (e.g. the {@link Service} is
     * not exported or protected from other applications by signature-level permission).
     * 2) Clients never leak received {@link Binder} to untrusted processes.
     *
     * A good example is an application, internally split in several processes, all of which
     * execute only trusted code. In contrast, do set this to false, when you expect third party
     * code (such as plugins) to communicate with yours via the annotated interface.
     *
     * Default is false.
     */
    boolean insecure() default false;

    /**
     * Whether to consider method parameters and return values nullable by default.
     *
     * Setting this to false will omit some null checks from generated code and may result
     * in NullPointerExceptions within generated code, if the contract is violated.
     *
     * Default is true.
     */
    boolean defaultNullable() default true;
}
