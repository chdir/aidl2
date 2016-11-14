package net.sf.aidl2;

import com.google.testing.compile.JavaFileObjects;

import net.sf.aidl2.internal.AidlProcessor;
import net.sf.aidl2.internal.Config;
import net.sf.aidl2.tests.LogFileRule;

import org.junit.Rule;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class SetTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void concreteSetComplexDeserialization() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ConcreteSetTypeArgsAndTypeLoss.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ConcreteSetTypeArgsAndTypeLoss$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ConcreteSetTypeArgsAndTypeLoss$$AidlClientImpl.java"));

        // multiple (~4) raw type warnings due to using raw Callable (maybe remove?) and raw delegate
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-rawtypes",
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteSetComplexTypeLoss() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ConcreteSetTypeArgsAndTypeLoss2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ConcreteSetTypeArgsAndTypeLoss2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ConcreteSetTypeArgsAndTypeLoss2$$AidlClientImpl.java"));

        // couple of raw type warnings due to using raw delegate
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-rawtypes",
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
