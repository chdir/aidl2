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

public class MiscTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void asyncMethod() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("AsyncMethod.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("AsyncMethod$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("AsyncMethod$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void customDescriptor() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("CustomDescriptor.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("CustomDescriptor$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("CustomDescriptor$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void insecureAidl() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("InsecureAidl2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("InsecureAidl2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("InsecureAidl2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void compileComplex() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("AllTogether.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutError();
    }
}
