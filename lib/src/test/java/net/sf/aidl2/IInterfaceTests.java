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

public class IInterfaceTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    private String[] usualArgs() {
        return new String[] {
                "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                "-Xlint:all"
        };
    }

    @Test
    public void aidl2IInterfaceParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IInterfaceTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("IInterfaceTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("IInterfaceTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void aidl2IInterfaceReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IInterfaceTest2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("IInterfaceTest2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("IInterfaceTest2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
