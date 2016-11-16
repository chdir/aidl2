package net.sf.aidl2.internal.util;

import java.io.Closeable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// addition-only, cloneable, lazily expandable collection
// reading is fast, growing is slightly less fast, compared to ArrayList
// rudimentary error detection via ref-counting
public final class CowCloneableList<T> extends AbstractList<T> implements Closeable, Cloneable {
    private static final int DEFAULT_CAPACITY = 2;

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private static final Object[] EMPTY = {};

    @SuppressWarnings("unchecked")
    private T[] contents = (T[]) EMPTY;

    private int size;

    private int refCount;
    private CowCloneableList<T> parent;

    public CowCloneableList() {
    }

    private CowCloneableList(CowCloneableList<T> parent) {
        // no need to track parenthood for empty lists, they can not harm us anyway
        if (parent.contents != EMPTY) {
            this.parent = parent;
            this.contents = parent.contents;
            this.size = parent.size;

            ++parent.refCount;
        }
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacityInternal(int minCapacity) {
        checkRefCount();

        if (parent != null) {
            if (minCapacity - contents.length > 0) {
                // the copy is being done for us by during array expansion
                grow(minCapacity);
            } else {
                // do the copy ourselves
                T[] old = contents;
                contents = (T[]) new Object[old.length];
                System.arraycopy(old, 0, contents, 0, old.length);
            }
            parent.refCount--;
        } else {
            if (contents == EMPTY) {
                minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
            }

            ensureExplicitCapacity(minCapacity);
        }
    }

    private void checkRefCount() {
        if (refCount != 0) {
            throw new IllegalStateException(illegalRefCountMsg());
        }
    }

    private String illegalRefCountMsg() {
        return "Attempting to modify a CowCloneableList while " + refCount + " children are still alive";
    }

    private void ensureExplicitCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - contents.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = contents.length;

        int newCapacity = oldCapacity + (oldCapacity >> 1);

        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;

        contents = Arrays.copyOf(contents, newCapacity);
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    @Override
    public boolean add(T t) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        contents[size++] = t;
        return true;
    }

    @Override
    public T get(int index) {
        rangeCheck(index);

        return contents[index];
    }

    private void rangeCheck(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void close() {
        if (parent != null) {
            --parent.refCount;
        }
    }

    @Override
    @SuppressWarnings("all")
    public CowCloneableList<T> clone() {
        return new CowCloneableList<T>(this);
    }
}
