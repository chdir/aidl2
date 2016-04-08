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

    @Test
    public void objectReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("TheObjectReturn.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("TheObjectReturn$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("TheObjectReturn$$AidlClientImpl.java"));

        // two javac warnings because of using raw delegate
        // one javac warning because of redundant cast of return value
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void methodTypeArgumentParameterObject() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargTheObject.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargTheObject$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(ParcelableTests.class.getResource("MethodTypeargTheObject$$AidlClientImpl.java"));

        // two javac warnings because of using raw delegate
        // one javac warning because of redundant cast of return value
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
