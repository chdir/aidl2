package net.sf.aidl2;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.Iterator;

public class WeirdCollection extends AbstractCollection<String> {
    private WeirdCollection(String heh) {}

    @Override @NotNull
    public Iterator<String> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }
}
