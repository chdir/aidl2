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

public class FloatTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    private String[] usualArgs() {
        return new String[] {
                "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                "-Aaidl2_use_versioning=false",
                "-Xlint:all"
        };
    }

    @Test
    public void floatParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("FloatTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("FloatTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("FloatTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    //@Test
    public void floatReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("FloatTest2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("FloatTest2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("FloatTest2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    //@Test
    public void floatVarargs() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("FloatTest3.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("FloatTest3$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("FloatTest3$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
