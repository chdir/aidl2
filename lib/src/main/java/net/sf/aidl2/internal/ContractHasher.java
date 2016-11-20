package net.sf.aidl2.internal;

import net.sf.aidl2.internal.util.NullStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Attempts to unambiguously encode AIDL2 interface contracts as series of bytes. The resulting byte
 * array is hashed with SHA and converted to single long value, which can be used for the same purpose
 * as serialVersionUID.
 *
 * Matching server/client version codes do not actually guarantee binary compatibility, because this hash
 * covers only AIDL2-specific ABI details. Changes to format of Parcelable/Externalizable/Serializable classes
 * may continue to cause hard-to-diagnose issues unless controlled by some other means.
 */
final class ContractHasher extends DataOutputStream {
    // Some types of arguments may contain additional metadata after them (e.g. arrays types are followed by encoding
    // for array component, maps are followed by encoding for key and value etc.)

    // the following integer is transaction id, followed by bunch of other metadata
    public static final int TRANSACTION_ID = -1;

    // the following input encodes an argument (either NULL_CHECK or integer ordinal in ArgumentKind will follow)
    public static final int INPUT_ARGUMENT = -2;

    // the following input encodes a return value (either NULL_CHECK or integer ordinal in ArgumentKind will follow)
    public static final int RET_VAL= -3;

    // the following integer is ordinal in ArgumentKind
    public static final int NULL_CHECK = -4;

    // the following integer is ordinal in TypeKind
    public static final int PRIMITIVE = -5;

    // the following integer is ordinal in TypeKind
    public static final int PRIMITIVE_ARRAY = -6;

    // the following bytes contain null-terminated name of class in modified UTF-8
    public static final int TYPE_NAME = -7;

    private final MessageDigest sha;

    private ContractHasher(MessageDigest sha) {
        super(new DigestOutputStream(new NullStream(), sha));

        this.sha = sha;
    }

    static ContractHasher create() {
        try {
            return new ContractHasher(MessageDigest.getInstance("SHA-1"));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private boolean closed;

    public long computeDigest() throws IOException {
        if (closed) {
            throw new IllegalStateException("Already computed digest");
        }

        close();

        closed = true;

        final byte[] hashBytes = sha.digest();

        long hash = 0;

        for (int i = Math.min(hashBytes.length, 8) - 1; i >= 0; i--) {
            hash = (hash << 8) | (hashBytes[i] & 0xFF);
        }

        return hash;
    }
}
