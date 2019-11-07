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

public class ExplicitConversionTests {
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
    public void intAsSerializableParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IntAsSerializableTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("IntAsSerializableTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("IntAsSerializableTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void serializableAsArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("SerializableAsArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("SerializableAsArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("SerializableAsArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void serializableAsCollectionParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("SerializableAsCollectionTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("SerializableAsCollectionTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("SerializableAsCollectionTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void parcelableAsCollectionReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ParcelableAsCollectionTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ParcelableAsCollectionTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ParcelableAsCollectionTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void parcelableAsMapReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ParcelableAsMapTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ParcelableAsMapTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ParcelableAsMapTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
