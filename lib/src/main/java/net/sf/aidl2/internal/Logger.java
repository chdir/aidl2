package net.sf.aidl2.internal;

import net.sf.aidl2.internal.exceptions.FaultyAnnotation;
import net.sf.aidl2.internal.exceptions.FaultyAnnotationValue;
import net.sf.aidl2.internal.exceptions.FaultyElement;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.sql.Date;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

class Logger implements Closeable {
    private static final String TAG = "AIDL2 aborted compilation. ";
    private static final String TAG2 = "AIDL2 says: ";

    private final Messager messager;
    private final Config cfg;

    private boolean abortedAlready;

    private String makeTag(Diagnostic.Kind diagnostic) {
        if (diagnostic == Diagnostic.Kind.ERROR && !abortedAlready) {
            abortedAlready = true;
            return TAG;
        } else {
            return TAG2;
        }
    }

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
                } else if (logFile.isAbsolute() && (logFile.createNewFile() || logFile.exists())) {
                    result = new FileLogger(env, logFile);
                }
            }

            if (result != null) {
                return result;
            }
        } catch (IOException e) {
            env.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Failed to open log file: " + messageFor(e) + ", disabling file logging");
        }

        return new Logger(env);
    }

    public void log(FaultyAnnotationValue... issues) {
        for (FaultyAnnotationValue issue : issues) {
            messager.printMessage(issue.getKind(), makeTag(issue.getKind()) + issue.getMessage(), issue.getElement(), issue.getAnnotation(), issue.getValue());
        }
    }

    public void log(FaultyAnnotation... issues) {
        for (FaultyAnnotation issue : issues) {
            messager.printMessage(issue.getKind(), makeTag(issue.getKind()) + issue.getMessage(), issue.getElement(), issue.getAnnotation());
        }
    }

    public void log(FaultyElement... issues) {
        for (FaultyElement issue : issues) {
            final String message = makeTag(issue.getKind()) + "\"" +
                    issue.getElement().getSimpleName() + "\" — " + issue.getMessage();

            messager.printMessage(issue.getKind(), message, issue.getElement());
        }
    }

    public void log(String note) {
        messager.printMessage(Diagnostic.Kind.NOTE, TAG2 + note);
    }

    public void log(Throwable... errors) {
        for (Throwable issue : errors) {
            messager.printMessage(Diagnostic.Kind.ERROR, TAG2 + issue.getMessage());

            if (cfg.isVerbose()) {
                issue.printStackTrace();
            }
        }
    }

    public URI getLogFile() {
        return null;
    }

    private static class FileLogger extends Logger {
        private final PrintWriter logStream;
        private final Elements elements;
        private final URI fileUrl;

        public FileLogger(AidlProcessor.Environment pe) throws IOException {
            this(pe, createLogFile(pe));
        }

        public FileLogger(AidlProcessor.Environment env, File logFile) throws IOException {
            this(env, new PrintWriter(logFile), logFile.toURI());
        }

        private FileLogger(AidlProcessor.Environment pe, FileObject logFileObject) throws IOException {
            this(pe, new PrintWriter(logFileObject.openWriter()), logFileObject.toUri());
        }

        private FileLogger(AidlProcessor.Environment pe, PrintWriter logStream, URI logUri) throws IOException {
            super(pe);

            this.fileUrl = logUri;

            this.logStream = logStream;

            this.elements = pe.getElementUtils();
        }

        @Override
        public URI getLogFile() {
            return fileUrl;
        }

        @Override
        public void log(FaultyAnnotationValue... issues) {
            super.log(issues);

            for (FaultyAnnotationValue val : issues) {
                log(val);
            }

            sync();
        }

        @Override
        public void log(FaultyAnnotation... issues) {
            super.log(issues);

            for (FaultyAnnotation ann : issues) {
                log(ann);
            }

            sync();
        }

        @Override
        public void log(FaultyElement... issues) {
            super.log(issues);

            for (FaultyElement el : issues) {
                log(el);
            }

            sync();
        }

        @Override
        public void log(String note) {
            super.log(note);

            logToFile("[NOTE] ", note);

            sync();
        }

        private void sync() {
            logStream.flush();
        }

        private void log(FaultyAnnotationValue issue) {
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

            sync();
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

        private static FileObject createLogFile(ProcessingEnvironment pe) throws IOException {
            final Filer filer = pe.getFiler();

            return filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "aidl2.log");
        }
    }
}
