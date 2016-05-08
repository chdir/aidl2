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

public class ListTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void abstractListsSimple() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void abstractListNested() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListSimpleNested.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListSimpleNested$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListSimpleNested$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void abstractListTypeMismatch() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListSimpleTypeMismatch.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListSimpleTypeMismatch$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("AbstractListSimpleTypeMismatch$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteListMethodTypeParam() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteListMethodTypeParam.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteListMethodTypeParam$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteListMethodTypeParam$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteListClassTypeParam() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteListClassTypeParamAndRaw.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteListClassTypeParamAndRaw$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteListClassTypeParamAndRaw$$AidlClientImpl.java"));

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
