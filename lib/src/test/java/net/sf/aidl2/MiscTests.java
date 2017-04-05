package net.sf.aidl2;

import com.google.testing.compile.JavaFileObjects;

import net.sf.aidl2.internal.AidlProcessor;
import net.sf.aidl2.internal.Config;
import net.sf.aidl2.tests.LogFileRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class MiscTests {
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
    public void asyncMethod() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AsyncMethod.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("AsyncMethod$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("AsyncMethod$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void customDescriptor() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("CustomDescriptor.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("CustomDescriptor$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("CustomDescriptor$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void insecureAidl() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("InsecureAidl2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("InsecureAidl2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("InsecureAidl2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void empty() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("Empty.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("Empty$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("Empty$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void allowDeclaringRuntimeExceptionsInMethodSignature() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("RuntimeExceptionAllowed.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings();
    }

    @Test
    public void compileComplex() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AllTogether.java"));

        // too complex to track warnings :)
        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutError();
    }

    @Test
    public void nothing() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forSourceLines("Nothing.java");

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings();
    }

    @Test
    public void localNameIsolation() throws Exception {
        JavaFileObject testSource1 = JavaFileObjects.forResource(getClass().getResource("MethodAndParamNameIsolation1.java"));
        JavaFileObject testSource2 = JavaFileObjects.forResource(getClass().getResource("MethodAndParamNameIsolation2.java"));

        JavaFileObject generatedStub1 = JavaFileObjects.forResource(getClass().getResource("MethodAndParameterNameIsolation1$$AidlServerImpl.java"));
        JavaFileObject generatedProxy1 = JavaFileObjects.forResource(getClass().getResource("MethodAndParameterNameIsolation1$$AidlClientImpl.java"));
        JavaFileObject generatedStub2 = JavaFileObjects.forResource(getClass().getResource("MethodAndParameterNameIsolation2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy2 = JavaFileObjects.forResource(getClass().getResource("MethodAndParameterNameIsolation2$$AidlClientImpl.java"));

        assertAbout(javaSources()).that(Arrays.asList(testSource1, testSource2))
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub1, generatedProxy1, generatedStub2, generatedProxy2);
    }

    @Test
    public void nullabilityInheritance() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NullabilityInheritance.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("NullabilityInheritance$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("NullabilityInheritance$$AidlClientImpl.java"));

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
    public void nonFinalConcreteTypes() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NonFinalAsAbstract.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("NonFinalAsAbstract$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("NonFinalAsAbstract$$AidlClientImpl.java"));

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
    public void assumedFinalConcreteTypes() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NonFinalAsConcrete.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("NonFinalAsConcrete$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("NonFinalAsConcrete$$AidlClientImpl.java"));

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
    public void methodOrderStabilityAndInheritance() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("TransactionIdOrder.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("TransactionIdOrder$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("TransactionIdOrder$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void customTransactionIds() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AdjustingTransactionIds.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("AdjustingTransactionIds$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("AdjustingTransactionIds$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void warnings() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MoreSuppressingWarnings.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("MoreSuppressingWarnings$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("MoreSuppressingWarnings$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Aaidl2_use_versioning=false",
                        "-Xlint:-rawtypes"
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void ipcVersionIdStability() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IpcVersionStability.java"));
        JavaFileObject testSource2 = JavaFileObjects.forResource(getClass().getResource("IpcVersionStability2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("IpcVersionStability$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("IpcVersionStability$$AidlClientImpl.java"));
        JavaFileObject generatedStub2 = JavaFileObjects.forResource(getClass().getResource("IpcVersionStability2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy2 = JavaFileObjects.forResource(getClass().getResource("IpcVersionStability2$$AidlClientImpl.java"));

        assertAbout(javaSources())
                .that(Arrays.asList(testSource, testSource2))
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-processing",
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy, generatedStub2, generatedProxy2);
    }
}
