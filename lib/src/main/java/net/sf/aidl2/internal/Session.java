package net.sf.aidl2.internal;

import com.squareup.javapoet.JavaFile;
import net.sf.aidl2.AIDL;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.exceptions.AnnotationException;
import net.sf.aidl2.internal.exceptions.AnnotationValueException;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.exceptions.ElementException;
import net.sf.aidl2.internal.util.Util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import static javax.tools.Diagnostic.Kind.NOTE;

final class Session extends AptHelper implements Closeable {
    // see https://github.com/chdir/aidl2/wiki/Limits
    private static final int METHOD_COUNT_CAP = 9000;

    private Set<Name> pendingElements = new HashSet<>();

    public Session(AidlProcessor.Environment environment) {
        super(environment);
    }

    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment)
            throws ElementException, AnnotationException, AnnotationValueException, IOException {
        if (set.isEmpty()) {
            getBaseEnvironment().getMessager().printMessage(NOTE, "Called with empty root element set");
        }

        final HashSet<Element> workingSet = new HashSet<>();

        workingSet.addAll(roundEnvironment.getElementsAnnotatedWith(AIDL.class));

        if (!pendingElements.isEmpty()) {
            for (Name pending : pendingElements) {
                Element pendingInterface = elements.getTypeElement(pending);

                if (pendingInterface == null) {
                    throw new IllegalStateException("Failed to locate by name previously seen " + pending);
                }

                workingSet.add(pendingInterface);
            }
        }

        if (workingSet.isEmpty()) {
            return true;
        }

        getBaseEnvironment().getLogger()
                .log("Started new session at " + new Date(System.currentTimeMillis()));

        final List<AidlModel> producedModels = new ArrayList<>(workingSet.size());

        final AidlValidator typeValidator = new AidlValidator(getBaseEnvironment());

        for (Element annotated : workingSet) {
            TypeElement iInterface = (TypeElement) annotated;

            Name qualified = iInterface.getQualifiedName();

            if (qualified == null) {
                throw new IllegalStateException("Failed to get qualified name of annotated interface " + iInterface);
            }

            DeclaredType interfaceType = (DeclaredType) iInterface.asType();

            Element erroneousElement;
            if ((erroneousElement = typeValidator.validate(iInterface)) == null) {
                final List<TypeInvocation<ExecutableElement, ExecutableType>> methods = gatherAidl2Methods(interfaceType);

                final AidlMethodValidator methodValidator =
                        new AidlMethodValidator(getBaseEnvironment(), (DeclaredType) iInterface.asType());

                for (TypeInvocation<ExecutableElement, ExecutableType> methodEl : methods) {
                    methodValidator.validate(methodEl);
                }

                producedModels.add(AidlModel.create(iInterface, methods, new MethodComparator()));

                pendingElements.remove(qualified);
            } else if (roundEnvironment.processingOver()) {
                throw new ElementException(
                        "Looks like there are compilation errors in the @AIDL-annotated interface, please fix that.", erroneousElement);
            } else {
                pendingElements.add(qualified);

                return false;
            }
        }

        final AidlGenerator stubGen = new StubGenerator(getBaseEnvironment());
        final AidlGenerator proxyGen = new ProxyGenerator(getBaseEnvironment());
        final List<JavaFile> generatedFiles = new ArrayList<>(2);

        for (AidlModel model : producedModels) {
            stubGen.make(generatedFiles, model);
            proxyGen.make(generatedFiles, model);
            model.finishGeneration();

            for (JavaFile generated : generatedFiles) {
                generated.writeTo(getFiler());
            }

            generatedFiles.clear();
        }

        return true;
    }

    private List<TypeInvocation<ExecutableElement, ExecutableType>> gatherAidl2Methods(DeclaredType element) throws ElementException {
        TypeElement type = (TypeElement) element.asElement();

        final List<ExecutableElement> allMethods = ElementFilter.methodsIn(elements.getAllMembers(type));

        if (allMethods.size() >= METHOD_COUNT_CAP) {
            throw new ElementException(type.getSimpleName() + " exceeds maximum number of methods supported by AIDL2", type);
        }

        for (Iterator<ExecutableElement> it = allMethods.iterator(); it.hasNext() ;)
        {
            if (!Util.isAIDL2method(it.next())) {
                it.remove();
            }
        }

        final List<TypeInvocation<ExecutableElement, ExecutableType>> aidl2Methods = new ArrayList<>(allMethods.size());

        for (ExecutableElement method : allMethods) {
            final ExecutableType methodType = (ExecutableType) types.asMemberOf(element, method);

            final TypeInvocation<ExecutableElement, ExecutableType> typeInvocation =
                    new TypeInvocation<>(method, methodType);

            if (isSubsignature(typeInvocation, asBinder)) continue;

            aidl2Methods.add(typeInvocation);
        }

        return aidl2Methods;
    }

    private final class MethodComparator implements Comparator<AidlMethodModel> {
        @Override
        public int compare(AidlMethodModel t1, AidlMethodModel t2) {
            final ExecutableElement e1 = t1.element.element;
            final ExecutableElement e2 = t2.element.element;

            final int x = t1.idxInFile;
            final int y = t2.idxInFile;

            if (x != -1 && y != -1) {
                return Integer.compare(x, y);
            }

            if (x != -1) return -1;
            if (y != -1) return +1;

            // neither of methods are present in top-level class, sort alphanumerically
            final String nam1 = e1.getSimpleName().toString();
            final String nam2 = e2.getSimpleName().toString();

            final int nameResult = nam1.compareTo(nam2);

            if (nameResult != 0) {
                return nameResult;
            }

            final List<? extends VariableElement> e1params = e1.getParameters();
            final List<? extends VariableElement> e2params = e2.getParameters();

            final int x1 = e1params.size();
            final int y1 = e2params.size();

            final int paramCountResult = Integer.compare(x1, y1);

            if (paramCountResult != 0) {
                return paramCountResult;
            }

            return compareParameters(e1params, e2params);
        }

        private int compareParameters(List<? extends VariableElement> e1params, List<? extends VariableElement> e2params) {
            Iterator<? extends VariableElement> e2paramIter = e2params.iterator();

            for (VariableElement e1param : e1params) {
                int comparisonResult = compareParameter(e1param, e2paramIter.next());

                if (comparisonResult != 0) {
                    return comparisonResult;
                }
            }

            return 0;
        }

        private int compareParameter(VariableElement v1, VariableElement v2) {
            TypeMirror fullType1 = v1.asType();
            TypeMirror fullType2 = v2.asType();

            if (fullType1.equals(fullType2)) {
                return 0;
            }

            final TypeKind kind1 = fullType1.getKind();
            final TypeKind kind2 = fullType2.getKind();

            if (!kind1.isPrimitive() && !kind2.isPrimitive()) {
                final TypeMirror erasure1 = types.erasure(fullType1);
                final TypeMirror erasure2 = types.erasure(fullType2);

                final int erased = erasure1.toString().compareTo(erasure2.toString());

                if (erased != 0) {
                    return erased;
                }
            }

            if (kind1 != kind2) {
                return kind1.compareTo(kind2);
            }

            return fullType1.toString().compareTo(fullType2.toString());
        }
    }

    @Override
    public void close() {
    }
}
