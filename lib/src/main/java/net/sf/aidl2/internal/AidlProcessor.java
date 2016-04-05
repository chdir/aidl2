package net.sf.aidl2.internal;

import com.google.auto.service.AutoService;

import net.sf.aidl2.OneWay;
import net.sf.aidl2.internal.exceptions.ReadableException;
import net.sf.aidl2.AIDL;
import net.sf.aidl2.internal.exceptions.AnnotationValueException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public final class AidlProcessor extends AbstractProcessor {
    private Environment env;

    private Session session;

    @Override
    public Set<String> getSupportedOptions() {
        return Config.getOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotations = new HashSet<>();
        annotations.add(AIDL.class.getName());
        annotations.add(OneWay.class.getName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return Collections.emptyList();
    }

    @Override
    public void init(ProcessingEnvironment env) {
        this.env = new Environment(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.errorRaised()) {
            // neither other processors, nor compiler itself must depend on our output - bail early
            env.getLogger().log(new net.sf.aidl2.internal.exceptions.QuietException("Called with pending errors — bailing out."));

            cleanup();
        } else {
            try {
                return getSession().process(set, roundEnvironment);
            } catch (RuntimeException re) {
                env.getLogger().log(new ReadableException(emitBugreportRequest(re), re));
            } catch (IOException ioe) {
                env.getLogger().log(new ReadableException("IO error during processing: " + Logger.messageFor(ioe), ioe));
            } catch (AnnotationValueException e) {
                env.getLogger().log((net.sf.aidl2.internal.exceptions.FaultyAnnotationValue) e);
            } catch (net.sf.aidl2.internal.exceptions.AnnotationException e) {
                env.getLogger().log((net.sf.aidl2.internal.exceptions.FaultyAnnotation) e);
            } catch (net.sf.aidl2.internal.exceptions.ElementException e) {
                env.getLogger().log((net.sf.aidl2.internal.exceptions.FaultyElement) e);
            } finally {
                if (roundEnvironment.processingOver()) {
                    cleanup();
                }
            }
        }

        return true;
    }

    private String emitBugreportRequest(Throwable t) {
        final String reportRequest = "Unexpected runtime error: " + Logger.messageFor(t) +
                "; please report a bug at http://github.com/gprocessor/issues";

        final String logfileSnippet = env.config.isFileLoggingEnabled()
                ? ". Log file is at " + env.logger.getLogFile()
                : ". Make sure to enable logging by passing " + Config.OPT_LOGFILE + " to processor and attach created file";

        return reportRequest + logfileSnippet;
    }

    private void cleanup() {
        if (session != null) {
            session.close();
            session = null;
        }

        env.close();
    }

    private Session getSession() throws IOException {
        if (session == null) {
            session = new Session(env);
        }

        return session;
    }

    public static class Environment implements ProcessingEnvironment, Closeable {
        private final ProcessingEnvironment environment;

        private Logger logger;

        private Config config;

        public Environment(ProcessingEnvironment environment) {
            this.environment = environment;
        }

        public Logger getLogger() {
            if (logger == null) {
                logger = Logger.create(this);
            }

            return logger;
        }

        public Config getConfig() {
            if (config == null) {
                config = new Config(getOptions());
            }

            return config;
        }

        @Override
        public Map<String, String> getOptions() {
            return environment.getOptions();
        }

        @Override
        public Messager getMessager() {
            return environment.getMessager();
        }

        @Override
        public Filer getFiler() {
            return environment.getFiler();
        }

        @Override
        public Elements getElementUtils() {
            return environment.getElementUtils();
        }

        @Override
        public Types getTypeUtils() {
            return environment.getTypeUtils();
        }

        @Override
        public SourceVersion getSourceVersion() {
            return environment.getSourceVersion();
        }

        @Override
        public Locale getLocale() {
            return environment.getLocale();
        }

        @Override
        public void close() {
            if (logger != null) {
                logger.close();
                logger = null;
            }
        }
    }
}
