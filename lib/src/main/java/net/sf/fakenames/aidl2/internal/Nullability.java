package net.sf.fakenames.aidl2.internal;

import javax.lang.model.type.TypeMirror;

public enum Nullability {
    ACCOUNT_FOR {
        @Override
        public boolean shouldCheckForNull(TypeMirror type) {
            return true;
        }
    },
    IGNORE {
        @Override
        public boolean shouldCheckForNull(TypeMirror type) {
            return false;
        }
    },
    MAY_ACCOUNT_FOR {
        @Override
        public boolean shouldCheckForNull(TypeMirror type) {
            return !net.sf.fakenames.aidl2.internal.util.JavaVersion.atLeast(net.sf.fakenames.aidl2.internal.util.JavaVersion.JAVA_1_9) || net.sf.fakenames.aidl2.internal.util.Util.isNullable(type, true);
        }
    };

    public abstract boolean shouldCheckForNull(TypeMirror type);

    public static Nullability just(boolean binaryNullable) {
        return binaryNullable ? ACCOUNT_FOR : IGNORE;
    }
}
