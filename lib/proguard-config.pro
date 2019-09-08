-repackageclasses net.sf.aidl2.internal

-optimizationpasses 6
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-adaptresourcefilecontents
-useuniqueclassmembernames
-keepparameternames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keep public class net.sf.aidl2.* {
    !private <methods>;
    !private <fields>;
}
-keep,allowoptimization,allowobfuscation public class net.sf.aidl2.internal.AidlProcessor