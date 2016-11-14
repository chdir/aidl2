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

public class ObjectTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    private String[] usualArgs() {
        return new String[] {
                "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                "-Xlint:all"
        };
    }

    @Test
    public void objectReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("TheObjectReturn.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("TheObjectReturn$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("TheObjectReturn$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void methodTypeArgumentParameterObject() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MethodTypeargTheObject.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("MethodTypeargTheObject$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("MethodTypeargTheObject$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
