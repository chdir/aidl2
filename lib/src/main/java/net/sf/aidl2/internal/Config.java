package net.sf.aidl2.internal;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;

public final class Config {
    public static final String OPT_LOGFILE = "aidl2_log_to_file";
    public static final String OPT_PRINT_TRACE = "aidl2_verbose";
    public static final String OPT_LIBRARY = "aidl2_library";
    public static final String OPT_SUPPRESS_ALL = "aidl2_dontwarn";
    public static final String OPT_PKG_NAME = "aidl2_dontwarn";
    public static final String OPT_DISABLE_EASTER_EGGS = "aidl2_no_jokes";

    public static Set<String> getOptions() {
        final Set<String> options = new HashSet<>(4, 1f);
        options.add(OPT_LOGFILE);
        options.add(OPT_LIBRARY);
        options.add(OPT_SUPPRESS_ALL);
        options.add(OPT_PKG_NAME);
        return options;
    }

    private boolean isLibrary;
    private boolean noWarn;
    private boolean fileLoggingEnabled;
    private File logFile;
    private boolean verbose;
    private boolean humorous;

    public Config(Map<String, String> options) {
        isLibrary = Boolean.valueOf(options.get(OPT_LIBRARY));
        noWarn = Boolean.valueOf(options.get(OPT_SUPPRESS_ALL));
        fileLoggingEnabled = Boolean.valueOf(options.get(OPT_LOGFILE));
        String logFileName = options.get(OPT_LOGFILE);
        if (!fileLoggingEnabled && logFileName != null && !logFileName.isEmpty()) {
            fileLoggingEnabled = true;
            logFile = new File(logFileName);
        }
        verbose = Boolean.valueOf(options.get(OPT_PRINT_TRACE));
        humorous = !Boolean.valueOf(OPT_DISABLE_EASTER_EGGS);
    }

    public boolean isLibrary() {
        return isLibrary;
    }

    public void setLibrary(boolean isLibrary) {
        this.isLibrary = isLibrary;
    }

    public boolean isNoWarn() {
        return noWarn;
    }

    public void setNoWarn(boolean noWarn) {
        this.noWarn = noWarn;
    }

    public boolean isFileLoggingEnabled() {
        return fileLoggingEnabled;
    }

    public File getLogFile() {
        return fileLoggingEnabled ? logFile : null;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isHumorous() {
        return humorous;
    }
}
