package net.sf.aidl2.internal.util;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.SourceVersion;

public enum JavaVersion {
    JAVA_1_6(KnownJavaVersion.JAVA_1_6.value, KnownJavaVersion.JAVA_1_6.name),
    JAVA_1_7(KnownJavaVersion.JAVA_1_7.value, KnownJavaVersion.JAVA_1_7.name),
    JAVA_1_8(KnownJavaVersion.JAVA_1_8.value, KnownJavaVersion.JAVA_1_8.name),
    JAVA_1_9(KnownJavaVersion.JAVA_1_9.value, KnownJavaVersion.JAVA_1_9.name),
    JAVA_LATEST_SUPPORTED(maxVersion(), SourceVersion.latest().name());

    public static float maxVersion() {
        final String lastValidName = SourceVersion.latest().name();

        for (KnownJavaVersion knownVersion : KnownJavaVersion.values()) {
            if (knownVersion.name.equals(lastValidName)) {
                return knownVersion.value;
            }
        }

        return Float.MAX_VALUE;
    }

    private final float value;
    private final String name;

    JavaVersion(final float value, final String name) {
        this.value = value;
        this.name = name;
    }

    public static boolean atLeast(final JavaVersion requiredVersion) {
        return JAVA_LATEST_SUPPORTED.value >= requiredVersion.value;
    }

    public static boolean greaterThen(final JavaVersion requiredVersion) {
        return JAVA_LATEST_SUPPORTED.value > requiredVersion.value;
    }

    @Override
    public @NotNull String toString() {
        return name;
    }
}

enum KnownJavaVersion {
    // annotation processing was added in Java 1.6
    JAVA_1_6(1.6f, SourceVersion.RELEASE_6.name()),

    JAVA_1_7(1.7f, "RELEASE_7"),
    JAVA_1_8(1.8f, "RELEASE_8"),
    JAVA_1_9(1.9f, "RELEASE_9");

    final float value;
    final String name;

    KnownJavaVersion(final float value, final String name) {
        this.value = value;
        this.name = name;
    }
}