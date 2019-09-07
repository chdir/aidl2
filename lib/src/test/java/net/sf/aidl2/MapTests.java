package net.sf.aidl2;

import com.google.testing.compile.JavaFileObjects;
import net.sf.aidl2.internal.AidlProcessor;
import net.sf.aidl2.internal.Config;
import net.sf.aidl2.tests.LogFileRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class MapTests {
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
    public void mapBadKey() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MapBadKeyType.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorContaining("Map has unsupported key or value")
                .in(testSource)
                .onLine(10);
    }

    @Test
    public void mapBadValue() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MapBadValueType.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorContaining("Map has unsupported key or value")
                .in(testSource)
                .onLine(10);
    }

    @Test
    public void mapSerializable() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MapSerializable.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("MapSerializable$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("MapSerializable$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void mapSimple() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("SimpleMap.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("SimpleMap$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("SimpleMap$$AidlClientImpl.java"));

        assertAbout(javaSource()).that(testSource)
                .withCompilerOptions(usualArgs())
                .processedWith(new AidlProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedStub, generatedProxy);
    }

    @Test
    public void mapComplex() throws Exception {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MapAndNestedTypeArgs.java"));

        JavaFileObject generatedStub = JavaFileObjects.forResource(getClass().getResource("MapAndNestedTypeArgs$$AidlServerImpl.java"));
        JavaFileObject generatedProxy = JavaFileObjects.forResource(getClass().getResource("MapAndNestedTypeArgs$$AidlClientImpl.java"));

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
}
