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

public class BundleTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void bundleParameterAndReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("BundleTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("BundleTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("BundleTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
