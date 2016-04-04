package net.sf.fakenames.aidl2.internal;

import net.sf.fakenames.aidl2.internal.exceptions.FaultyAnnotation;
import net.sf.fakenames.aidl2.internal.exceptions.FaultyElement;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

class Logger implements Closeable {
    private static final String TAG = "AIDL2 has aborted compilation\n";

    private final Messager messager;
    private final Config cfg;

    private Logger(AidlProcessor.Environment pe) {
        this.messager = pe.getMessager();
        cfg = pe.getConfig();
    }

    public static String messageFor(Throwable t) {
        if (t instanceof OutOfMemoryError) {
            return "Out of memory";
        }

        final String message = t.getLocalizedMessage();

        return message != null && !message.isEmpty() ? message : t.getClass().getName();
    }

    public void close() {
    }

    public static Logger create(AidlProcessor.Environment env) {
        FileLogger result = null;

        final Config cfg = env.getConfig();

        try {
            if (cfg.isFileLoggingEnabled()) {
                final File logFile = cfg.getLogFile();

                if (logFile == null) {
                    result = new FileLogger(env);
                } else if (logFile.isAbsolute() && (logFile.createNewFile() && logFile.exists())) {
                    result = new FileLogger(env, logFile);
                }
            }

            if (result != null) {
                result.logToFile("[TRACE] Started new session at ", new Date(System.currentTimeMillis()));

                return result;
            }
        } catch (IOException e) {
            env.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Failed to open log file: " + messageFor(e) + ", disabling file logging");
        }

        return new Logger(env);
    }

    public void log(net.sf.fakenames.aidl2.internal.exceptions.FaultyAnnotationValue... issues) {
        for (net.sf.fakenames.aidl2.internal.exceptions.FaultyAnnotationValue issue : issues) {
            messager.printMessage(issue.getKind(), TAG + issue.getMessage(), issue.getElement(), issue.getAnnotation(), issue.getValue());
        }
    }

    public void log(FaultyAnnotation... issues) {
        for (FaultyAnnotation issue : issues) {
            messager.printMessage(issue.getKind(), TAG + issue.getMessage(), issue.getElement(), issue.getAnnotation());
        }
    }

    public void log(FaultyElement... issues) {
        for (FaultyElement issue : issues) {
            messager.printMessage(issue.getKind(), TAG + issue.getMessage(), issue.getElement());
        }
    }

    public void log(Throwable... errors) {
        for (Throwable issue : errors) {
            messager.printMessage(Diagnostic.Kind.ERROR, TAG + issue.getMessage());

            if (cfg.isVerbose()) {
                issue.printStackTrace();
            }
        }
    }

    private static class FileLogger extends Logger {
        private final PrintWriter logStream;
        private final Elements elements;

        private FileLogger(AidlProcessor.Environment pe, PrintWriter logStream) throws IOException {
            super(pe);

            this.logStream = logStream;

            this.elements = pe.getElementUtils();
        }

        public FileLogger(AidlProcessor.Environment pe) throws IOException {
            this(pe, createLogFile(pe));
        }

        public FileLogger(AidlProcessor.Environment env, File logFile) throws IOException {
            this(env, new PrintWriter(logFile));
        }

        @Override
        public void log(net.sf.fakenames.aidl2.internal.exceptions.FaultyAnnotationValue... issues) {
            super.log(issues);

            for (net.sf.fakenames.aidl2.internal.exceptions.FaultyAnnotationValue val : issues) {
                log(val);
            }
        }

        @Override
        public void log(FaultyAnnotation... issues) {
            super.log(issues);

            for (FaultyAnnotation ann : issues) {
                log(ann);
            }
        }

        @Override
        public void log(FaultyElement... issues) {
            super.log(issues);

            for (FaultyElement el : issues) {
                log(el);
            }
        }

        private void log(net.sf.fakenames.aidl2.internal.exceptions.FaultyAnnotationValue issue) {
            final AnnotationValue value = issue.getValue();
            final AnnotationMirror an = issue.getAnnotation();

            Object literalValue = value + " (not representable)";

            try { literalValue = value.getValue(); } catch (Throwable ignore) {}

            logToFile("[", issue.getKind(), "] ", issue);
            logToFile("[", issue.getKind(), "] ", "Caused by annotation value ", value, " with assigned value: ", literalValue);
            logToFile("[", issue.getKind(), "] ", "Within annotation ", an, " of type ", an.getAnnotationType());
            logToFile("[" ,issue.getKind(), "] ", "Annotated element:");

            elements.printElements(logStream, issue.getElement());

            if (issue.getKind() == Diagnostic.Kind.ERROR && issue instanceof Throwable) {
                log((Throwable) issue);
            }
        }

        private void log(FaultyAnnotation issue) {
            final AnnotationMirror am = issue.getAnnotation();

            logToFile("[", issue.getKind(), "] ", issue);
            logToFile("[", issue.getKind(), "] ", "Caused by annotation ", am, " of type ", am.getAnnotationType());
            logToFile("[", issue.getKind(), "] ", "Annotated element:");

            elements.printElements(logStream, issue.getElement());

            if (issue.getKind() == Diagnostic.Kind.ERROR && issue instanceof Throwable) {
                log((Throwable) issue);
            }
        }

        private void log(FaultyElement issue) {
            logToFile("[", issue.getKind(), "] ", issue);
            logToFile("[", issue.getKind(), "] ", "Caused by element:");

            elements.printElements(logStream, issue.getElement());

            if (issue.getKind() == Diagnostic.Kind.ERROR && issue instanceof Throwable) {
                log((Throwable) issue);
            }
        }

        @Override
        public void log(Throwable... errors) {
            super.log(errors);

            for (Throwable t : errors) {
                logToFile("\nStacktrace: ");

                t.printStackTrace(logStream);
            }
        }

        private void logToFile(Object... parts) {
            for (Object part : parts) {
                logStream.append(String.valueOf(part));
            }

            logStream.append('\n');
        }

        public void close() {
            logStream.close();
        }

        private static PrintWriter createLogFile(ProcessingEnvironment pe) throws IOException {
            final Filer filer = pe.getFiler();

            final FileObject logFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "processor.log");

            return new PrintWriter(logFile.openWriter());
        }
    }
}
