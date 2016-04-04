// net.sf.fakenames.MyAidl.aidl
package net.sf.fakenames;

// Declare any non-default types here with import statements

interface MyAidl {
    void basicTypesIn(int anInt, in byte[] wow);

    void basicTypesOut(int anInt, out byte[] wow);

    void basicTypesInOut(int anInt, inout byte[] wow);

    oneway void basicTypesOneway(int anInt, long aLong, boolean aBoolean, float aFloat,
                        double aDouble, String aString);

    int returning(int anInt, long aLong, boolean aBoolean, float aFloat,
                                                double aDouble, String aString);
}
