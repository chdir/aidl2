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

public class ComplexGenericTests {
    @Rule
    public LogFileRule logFile = new LogFileRule();

    // ? extends T, where T extends SomethingElseMeaningful
    @Test
    public void wildcardBoundByTypeVarParameterAndReturnValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("WildcardBoundByTypeVar.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("WildcardBoundByTypeVar$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("WildcardBoundByTypeVar$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions("-A" + Config.OPT_LOGFILE + "=" + logFile.getFile())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    // a recursion within type signature of parameter type
    @Test
    public void recursionInSupertypeOfTypeWithinParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("RecursiveType.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("RecursiveType$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("RecursiveType$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-rawtypes"
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    // ad-hoc recursion between method method type parameters
    @Test
    public void recursionBetweenTypeParametersWithinReturnType() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("RecursiveType2.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("RecursiveType2$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("RecursiveType2$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-rawtypes"
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    // a slightly simpler variation of same trick, now recursive type is sneaked into nested type parameter
    // and have no hidden recursive types in parents
    @Test
    public void nestedRecursiveTypeInParameter() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("RecursiveType3.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("RecursiveType3$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("RecursiveType3$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-rawtypes"
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    // recursive type A extending both IInterface and another recursive type B (which also happens to be IInterface)
    @Test
    public void extendingRecursiveTypeWithRecursiveType() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("RecursiveExtendingRecursive.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("RecursiveExtendingRecursive$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("RecursiveExtendingRecursive$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(new String[] {
                        "-A" + Config.OPT_LOGFILE + "=" + logFile.getFile(),
                        "-Xlint:-rawtypes"
                })
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }
}
