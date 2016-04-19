package net.sf.aidl2;

import com.google.testing.compile.JavaFileObjects;
import com.sun.tools.javac.util.ServiceLoader;

import net.sf.aidl2.tests.LogFileRule;

import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.Processor;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class AndroidInterfaceTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void oldAidlIInterfaceReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(AndroidInterfaceTests.class.getResource("IInterfaceTest3.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(AndroidInterfaceTests.class.getResource("IInterfaceTest3$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(AndroidInterfaceTests.class.getResource("IInterfaceTest3$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-Aaidl2_log_to_file=" + logFile.getFile())
                .processedWith(getAidl2Processor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    private static Processor getAidl2Processor() {
        ServiceLoader<Processor> loader = ServiceLoader.load(Processor.class, AndroidInterfaceTests.class.getClassLoader());
        
        for (Processor processor : loader) {
            if (processor.getSupportedAnnotationTypes().contains(AIDL.class.getName())) {
                return processor;
            }
        }

        throw new IllegalStateException("AIDL2 processor not on classpath");
    }
}
