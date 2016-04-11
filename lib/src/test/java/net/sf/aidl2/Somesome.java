package net.sf.aidl2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Somesome<G extends Somesome<? extends Somesome>> implements Externalizable {
    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {

    }

    @Override
    public void writeExternal(ObjectOutput output) throws IOException {

    }

    public static final class Ohreally extends Somesome<Ohreally> {

    }
}
