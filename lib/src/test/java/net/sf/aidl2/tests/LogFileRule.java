package net.sf.aidl2.tests;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;

public final class LogFileRule extends TestWatcher {
    private File file;

    @Override
    protected void starting(Description description) {
        super.starting(description);

        try {
            file = File.createTempFile("proc", ".log");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create temporary file", e);
        }
    }

    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);

        if (file != null) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        super.skipped(e, description);

        if (file != null) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
    }

    public File getFile() {
        return file;
    }
}
