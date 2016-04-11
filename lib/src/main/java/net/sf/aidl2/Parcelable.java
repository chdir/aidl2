package net.sf.aidl2;

import android.os.Parcel;
import android.os.Parcelable.Creator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that annotated type must be handled as concrete, final Parcelable class,
 * e.g. instantiated directly via the {@code CREATOR field}.
 *
 * Subtypes of Parcelable are already (de)serialized by calling {@link Parcel} methods by default.
 * Using this annotation has a special meaning: it ensures that instances of annotated type are
 * created by using {@link Creator}, declared in it's closest Parcelable supertype, even non final
 * or abstract one.
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.SOURCE)
public @interface Parcelable {
}
