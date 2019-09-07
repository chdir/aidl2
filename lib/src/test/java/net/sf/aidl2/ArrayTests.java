package net.sf.aidl2;

import com.google.testing.compile.JavaFileObjects;

import net.sf.aidl2.internal.AidlProcessor;
import net.sf.aidl2.internal.Config;
import net.sf.aidl2.tests.LogFileRule;

import org.junit.Rule;
import org.junit.Test;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import java.net.URL;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ArrayTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    private String[] usualArgs() {
        return new String[] {
                "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                "-Aaidl2_use_versioning=false",
                "-Xlint:all",
        };
    }

    @Test
    public void intArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IntArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("IntArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("IntArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void intArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IntArrayTest2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("IntArrayTest2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("IntArrayTest2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void byteArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ByteArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ByteArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ByteArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void floatArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("FloatArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("FloatArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("FloatArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void stringArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("StringArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("StringArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("StringArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void parametrizedArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ParametrizedArray.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ParametrizedArray$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ParametrizedArray$$AidlClientImpl.java"));

        // not enabling unchecked warnings, because casting generic arrays without warnings is impossible as of Java 8
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:rawtypes",
                        "-Aaidl2_use_versioning=false",
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void parametrizedArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ParametrizedArray2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ParametrizedArray2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ParametrizedArray2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void nonNullSizeArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NotNullSizeArrayParameter.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("NotNullSizeArrayParameter$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("NotNullSizeArrayParameter$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-processing",
                        "-Aaidl2_use_versioning=false",
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void sizeArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("SizeArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("SizeArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("SizeArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void charBiArrayReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("CharArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("CharArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("CharArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void booleanTriArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("BooleanArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("BooleanArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("BooleanArrayTest$$AidlClientImpl.java"));

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

    @Test
    public void serializableArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("SerializableArrayTest.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("SerializableArrayTest$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("SerializableArrayTest$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void parametrizedSerializableArrayParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ParametrizedSerialArray.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("ParametrizedSerialArray$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("ParametrizedSerialArray$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
