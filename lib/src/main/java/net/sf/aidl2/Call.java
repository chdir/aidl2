package net.sf.aidl2;

import android.os.IBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supplies additional metadata about annotated AIDL2 method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Call {
    /**
     * Transaction ID, corresponding to the annotated method.
     *
     * AIDL2 will automatically assign a valid stable transaction IDs based on order of methods in java file
     * (for methods in the main interface class) or method signatures (for inherited interface methods).
     * Passing a custom value allows to implement existing interfaces (when used with
     * {@linkplain AIDL#value custom interface name}) or handle predefined transaction types (such as
     * {@link IBinder#PING_TRANSACTION} or {@link AidlUtil#VERSION_TRANSACTION}).
     */
    int value() default -1;
}
