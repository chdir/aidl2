package net.sf.aidl2;

import com.google.testing.compile.JavaFileObjects;

import net.sf.aidl2.internal.AidlProcessor;

import org.junit.Test;

import java.util.Arrays;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class ErrorTests {
    @Test
    public void failsWhenAidlMethodDeclaresNoRemoteException() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NoExceptionFailure.java"));

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
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("UnsupportedExceptionFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("IOException")
                .in(testSource)
                .onLine(10)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenOneWayMethodIsNotVoid() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("OneWayMustBeVoid.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("void")
                .in(testSource)
                .onLine(7)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void failsWhenAidlDoesNotImplementIInterface() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NotIInterfaceFailure.java"));

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
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AIDLNonInterfaceFailure.java"));

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
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("RawObjectNotSuppressedFailure.java"));

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
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("UnknownTypeFailure.java"));

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
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("WeirdCollectionFailure.java"));

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
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("BaseIInterfaceFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("use more specific type")
                .in(testSource)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");

        JavaFileObject testSource2 = JavaFileObjects.forResource(getClass().getResource("BaseIInterfaceFailure2.java"));

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
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MissingTypeFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(3)
                .withErrorContaining("compilation errors in the @AIDL-annotated interface");
    }

    @Test
    public void failsWhenParameterTypeDoesNotExist() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("MissingTypeFailure2.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(3)
                .withErrorContaining("compilation errors in the @AIDL-annotated interface");
    }

    @Test
    public void failsWhenAnnotationValueDoesNotExist() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("FaultyAnnotationValueFailure.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(3)
                .withErrorContaining("compilation errors in the @AIDL-annotated interface");
    }

    @Test
    public void incorrectTransactionId() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IncorrectTransactionId.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("transaction id")
                .in(testSource)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void incorrectTransactionId2() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("IncorrectTransactionId2.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("transaction id")
                .in(testSource)
                .onLine(8)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void ConflictingTransactionIds() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("ConflictingTransactionIds.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("143")
                .in(testSource)
                .onLine(9)
                .and()
                .withNoteContaining("bailing out");
    }

    @Test
    public void InterfaceAsConverter() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("InterfaceConverterError.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("must be a class")
                .in(testSource)
                .onLine(8);
    }

    @Test
    public void AbstractAsConverter() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AbstractConverterError.java"));
        JavaFileObject converter = JavaFileObjects.forResource(getClass().getResource("AbstractConverter.java"));

        assertAbout(javaSources()).that(Arrays.asList(testSource, converter))
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("can not be used as converter: it is abstract")
                .in(testSource)
                .onLine(8);
    }

    @Test
    public void NoPublicConstructorInConverter() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("NoConstructorConverterError.java"));
        JavaFileObject converter = JavaFileObjects.forResource(getClass().getResource("NoConstructorConverter.java"));

        assertAbout(javaSources()).that(Arrays.asList(testSource, converter))
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("must have a no-arg constructor to be used as converter")
                .in(testSource)
                .onLine(8);
    }

    @Test
    public void AsAnnotationOnVoid() {
        JavaFileObject testSource = JavaFileObjects.forResource(getClass().getResource("AsAnnotationOnVoidError.java"));

        assertAbout(javaSource()).that(testSource)
                .processedWith(new AidlProcessor())
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("void")
                .in(testSource)
                .onLine(8);
    }
}
