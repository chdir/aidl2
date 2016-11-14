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

public class ParcelableTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    private String[] usualArgs() {
        return new String[] {
                "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                "-Xlint:all"
        };
    }

    @Test
    public void abstractParcelableParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AbstractParcelableParameter.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("AbstractParcelableParameter$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("AbstractParcelableParameter$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void abstractParcelableReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AbstractParcelableReturn.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("AbstractParcelableReturn$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("AbstractParcelableReturn$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteParcelableReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ConcreteParcelableReturn.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ConcreteParcelableReturn$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ConcreteParcelableReturn$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteParcelableParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ConcreteParcelableParameter.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ConcreteParcelableParameter$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ConcreteParcelableParameter$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void nonNullConcreteParcelableParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NotNullParcelableParameter.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("NotNullParcelableParameter$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("NotNullParcelableParameter$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-processing",
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void methodTypeArgumentParamAbstractParcelable() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MethodTypeargParcelable.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("MethodTypeargParcelable$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("MethodTypeargParcelable$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void methodTypeArgumentReturnAbstractParcelable() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MethodTypeargParcelable2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("MethodTypeargParcelable2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("MethodTypeargParcelable2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void classTypeArgumentReturnAbstractParcelable() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ClassTypeargParcelable.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ClassTypeargParcelable$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ClassTypeargParcelable$$AidlClientImpl.java"));

        // two javac warnings because of using raw delegate
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
