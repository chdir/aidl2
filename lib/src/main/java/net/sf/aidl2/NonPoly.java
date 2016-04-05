package net.sf.aidl2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates, that specified Parcelable type can be handled as if it was final, e.g.: either there
 * is no subclasses (note: your are better off just making the class final in that case) or any
 * subclass can be serialized by calling {@link android.os.Parcelable#writeToParcel} and
 * subsequently deserialized by calling {@link android.os.Parcelable.Creator#createFromParcel}
 * of the CREATOR instance of the base class. Using this annotation can allow you to serialize
 * abstract Parcelable subclasses without reflection as long as the base abstract class contains
 * CREATOR field.
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface NonPoly {
}
