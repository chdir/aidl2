package net.sf.aidl2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that annotated AIDL2 method must be executed asynchronously using Binder's one-way
 * transaction. Exception reporting is impossible. Supported on void methods only.
 *
 * This annotation has exactly same semantics as "oneway" qualifier of Android Interface Definition Language.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface OneWay {
}
