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

public class AndroidInterfaceTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void oldAidlIInterfaceReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(AndroidInterfaceTests.class.getResource("IInterfaceTest3.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(AndroidInterfaceTests.class.getResource("IInterfaceTest3$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(AndroidInterfaceTests.class.getResource("IInterfaceTest3$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
