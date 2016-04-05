package net.sf.aidl2.internal;

import net.sf.aidl2.internal.util.JavaVersion;
import net.sf.aidl2.internal.util.Util;

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
            return !JavaVersion.atLeast(JavaVersion.JAVA_1_9) || Util.isNullable(type, true);
        }
    };

    public abstract boolean shouldCheckForNull(TypeMirror type);

    public static Nullability just(boolean binaryNullable) {
        return binaryNullable ? ACCOUNT_FOR : IGNORE;
    }
}
