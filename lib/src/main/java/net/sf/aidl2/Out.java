package net.sf.aidl2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that annotated AIDL2 method parameter has to be serialized and passed back to caller
 * before returning from IPC call. Applicable to reference types only.
 *
 * Note, that this annotation achieves same behavior as "inout" qualifier of
 * Android Interface Definition Language. In AIDL2 all method parameters are ALWAYS serialized
 * when passed to IPC. Pure "out" parameters are not supported.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface Out {
}
