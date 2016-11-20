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

    private String[] usualArgs() {
        return new String[] {
                "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                "-Aaidl2_use_versioning=false",
        };
    }

    @Test
    public void abstractListsSimple() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AbstractListTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("AbstractListTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("AbstractListTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void abstractListNested() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AbstractListSimpleNested.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("AbstractListSimpleNested$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("AbstractListSimpleNested$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void abstractListTypeMismatch() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AbstractListSimpleTypeMismatch.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("AbstractListSimpleTypeMismatch$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("AbstractListSimpleTypeMismatch$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteListMethodTypeParam() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ConcreteListMethodTypeParam.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ConcreteListMethodTypeParam$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ConcreteListMethodTypeParam$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteListClassTypeParam() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ConcreteListClassTypeParamAndRaw.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ConcreteListClassTypeParamAndRaw$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ConcreteListClassTypeParamAndRaw$$AidlClientImpl.java"));

        // two javac warnings because of using raw delegate
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-rawtypes",
                        "-Aaidl2_use_versioning=false",
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
