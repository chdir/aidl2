package net.sf.aidl2;

import java.util.LinkedHashMap;

public class DumbMap extends LinkedHashMap<String, Void> {
    public DumbMap() {
        super(10, 1.0f);
    }
}
