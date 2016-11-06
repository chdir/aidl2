package net.sf.aidl2;

import com.google.testing.compile.JavaFileObjects;

import net.sf.aidl2.internal.AidlProcessor;
import net.sf.aidl2.internal.Config;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ErrorTests {
    @Test
    public void failsWhenAidlMethodDeclaresNoRemoteException() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("NoExceptionFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("RemoteException")
                .in(testSource)
                .onLine(7)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenAidlMethodDeclaresUnsupportedException() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("UnsupportedExceptionFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("IOException")
                .in(testSource)
                .onLine(9)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenAidlDoesNotImplementIInterface() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("NotIInterfaceFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("IInterface")
                .in(testSource)
                .onLine(3)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenAidlIsNotInterface() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("AIDLNonInterfaceFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("interfaces only")
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenRawobjectNoUnchecked() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("RawObjectNotSuppressedFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("unchecked")
                .in(testSource)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenTypeNotSupported() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("UnknownTypeFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("Runnable")
                .in(testSource)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenCollectionNoDefaultConstructor() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("WeirdCollectionFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("Collection")
                .in(testSource)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenEncounteringIInterface() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("BaseIInterfaceFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("use more specific type")
                .in(testSource)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");

        JavaFileObject testSource2 = JavaFileObjects.forResource(IntTests.class.getResource("BaseIInterfaceFailure2.java"));

        assertAbout(javaSource()).that(testSource2)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("use more specific type")
                .in(testSource2)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenReturnTypeDoesNotExist() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("MissingTypeFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(3)
                .withErrorContaining("compilation errors in the @AIDL-annotated interface");
    }

    @Test
    public void failsWhenParameterTypeDoesNotExist() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("MissingTypeFailure2.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(3)
                .withErrorContaining("compilation errors in the @AIDL-annotated interface");
    }

    @Test
    public void failsWhenAnnotationValueDoesNotExist() {
        JavaFileObject testSource = JavaFileObjects.forResource(IntTests.class.getResource("FaultyAnnotationValueFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(3)
                .withErrorContaining("compilation errors in the @AIDL-annotated interface");
    }
}
