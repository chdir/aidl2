package net.sf.aidl2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies how to treat a method argument or return value during (de)serialization.
 *
 * Applies only to outer type itself - serialization strategies for nested map/collection
 * elements and array components will still be chosen according to default priority order.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface As {
    DataKind value() default DataKind.AUTO;
}