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

public class ExternalizableTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void concreteExternalizableParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("ExternalizableTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("ExternalizableTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("ExternalizableTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
