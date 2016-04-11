package net.sf.aidl2;

import android.content.Context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that annotated AIDL2 method parameter (or return type) may be represented by
 * external type at runtime.
 *
 * "External types" are types not available in current application ClassLoader.
 * They have to be loaded from code of another application using ClassLoader created by
 * {@link Context#createPackageContext} method with {@link Context#CONTEXT_INCLUDE_CODE} and
 * {@link Context#CONTEXT_IGNORE_SECURITY} flags. The package to load those types
 * from is determined by passing and additional hidden parameter along with annotated one.
 *
 * Supported on parameters with non-final types only.
 *
 * Using this annotation in AIDL2 interfaces not marked as {@link AIDL#insecure}
 * will result in compilation error.
 *
 * <b>WARNING: USING THIS ANNOTATION HAS IMPORTANT SECURITY CONSEQUENCES!<b/>
 *
 * An arbitrary number of external classes from outside application will be loaded in context of
 * your process without any precautions whatsoever. An arbitrary code from that application will be
 * executed within your own application. Never use this if you plan to communicate with unreliable
 * clients, not controlled by yourself.
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.SOURCE)
public @interface External {
}
