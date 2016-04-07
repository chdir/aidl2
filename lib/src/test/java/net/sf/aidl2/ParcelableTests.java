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

    @Test
    public void abstractParcelableParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("AbstractParcelableParameter.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("AbstractParcelableParameter$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("AbstractParcelableParameter$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void abstractParcelableReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("AbstractParcelableReturn.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("AbstractParcelableReturn$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("AbstractParcelableReturn$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteParcelableReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("ConcreteParcelableReturn.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("ConcreteParcelableReturn$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("ConcreteParcelableReturn$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteParcelableParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("ConcreteParcelableParameter.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("ConcreteParcelableParameter$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("ConcreteParcelableParameter$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void methodTypeArgumentParamAbstractParcelable() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargParcelable.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargParcelable$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargParcelable$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void methodTypeArgumentReturnAbstractParcelable() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargParcelable2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargParcelable2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargParcelable2$$AidlClientImpl.java"));

        // one javac warning because of redundant cast of return value
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutError()
                .withWarningCount(1)
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void classTypeArgumentReturnAbstractParcelable() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("ClassTypeargParcelable.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("ClassTypeargParcelable$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("ClassTypeargParcelable$$AidlClientImpl.java"));

        // two javac warnings because of using raw delegate
        // one javac warning because of redundant cast of return value
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutError()
                .withWarningCount(3)
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
