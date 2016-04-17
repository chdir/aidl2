package net.sf.aidl2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public final class SomeExternalizable implements Externalizable, Runnable {
    private String nameOfCat;

    private int catsChildren;

    public SomeExternalizable(String nameOfCat, int catsChildren) {
        this.nameOfCat = nameOfCat;
        this.catsChildren = catsChildren;
    }

    public SomeExternalizable() {
    }

    @Override
    public void run() {
        ++catsChildren;
    }

    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        nameOfCat = input.readUTF();
        catsChildren = input.readInt();
    }

    @Override
    public void writeExternal(ObjectOutput output) throws IOException {
        output.writeUTF(nameOfCat);
        output.write(catsChildren);
    }
}
