package net.sf.aidl2.internal;

import com.squareup.javapoet.NameAllocator;

import javax.lang.model.type.TypeMirror;

class State implements Cloneable {
    private final net.sf.aidl2.internal.AidlProcessor.Environment environment;

    NameAllocator allocator;
    boolean external;
    boolean allowUnchecked;
    boolean nullable = true;
    boolean returnValue;
    TypeMirror type;
    CharSequence name;

    public State(net.sf.aidl2.internal.AidlProcessor.Environment environment, NameAllocator allocator) {
        this.environment = environment;
        this.allocator = allocator;
    }

    public State allowUnchecked(boolean allowUnchecked) {
        this.allowUnchecked = allowUnchecked;
        return this;
    }

    public State setParameter(AidlParamModel param) {
        this.returnValue = param.isReturn();
        this.name = param.name == null ? "returnValue" : allocator.get(param);
        this.type = param.type;
        return this;
    }

    public State external(boolean external) {
        this.external = external;
        return this;
    }

    public State nullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public State clone() {
        try {
            State state = (State) super.clone();

            state.allocator = allocator.clone();

            return state;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public net.sf.aidl2.internal.Reader buildReader(String name) {
        return new net.sf.aidl2.internal.Reader(environment, this, name);
    }

    public net.sf.aidl2.internal.Writer buildWriter(String name) {
        return new net.sf.aidl2.internal.Writer(environment, this, name);
    }
}
