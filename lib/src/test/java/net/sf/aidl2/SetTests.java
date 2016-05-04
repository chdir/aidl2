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

public class SetTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    @Test
    public void concreteSetComplexDeserialization() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteSetTypeArgsAndTypeLoss.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteSetTypeArgsAndTypeLoss$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteSetTypeArgsAndTypeLoss$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void concreteSetComplexTypeLoss() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteSetTypeArgsAndTypeLoss2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteSetTypeArgsAndTypeLoss2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(IntTests.class.getResource("ConcreteSetTypeArgsAndTypeLoss2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
