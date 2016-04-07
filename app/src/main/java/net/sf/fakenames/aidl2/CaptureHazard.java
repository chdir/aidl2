package net.sf.fakenames.aidl2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by uniqa on 4/1/16.
 */
public class CaptureHazard<T extends Iterable> {
    private T gggg;

    public <X extends Collection & Serializable, S extends Serializable & Collection> void getIt(List<? extends T> param) {
        List<X> g = null;
        List<S> e = null;
    }
}
