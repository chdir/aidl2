package net.sf.aidl2.tests;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        failed(e, description);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);

        System.err.println("Test failed, printing processor log to standard error\n");

        try {
            try (FileChannel fileCh = new FileInputStream(file).getChannel()) {
                final long totalSize = fileCh.size();

                for (long remaining = totalSize; remaining > 0;) {
                    remaining -= fileCh.transferTo(totalSize - remaining, remaining, Channels.newChannel(System.err));

                    if (remaining == 0) {
                        if (totalSize - fileCh.size() >= remaining) {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e1) {
            System.err.println("Failed to print log file to standard error\n");

            e1.printStackTrace();
        } finally {
            if (file != null) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    public File getFile() {
        return file;
    }
}
