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

public class ArrayTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void intArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("IntArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("IntArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("IntArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void intArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("IntArrayTest2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("IntArrayTest2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("IntArrayTest2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void byteArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("ByteArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("ByteArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("ByteArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void stringArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("StringArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("StringArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("StringArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
