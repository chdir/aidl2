package net.sf.aidl2.internal;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.exceptions.AnnotationException;
import net.sf.aidl2.internal.exceptions.AnnotationValueException;
import net.sf.aidl2.internal.exceptions.ElementException;
import net.sf.aidl2.internal.util.Util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.util.ElementFilter;

import static javax.tools.Diagnostic.Kind.NOTE;

final class Session extends AptHelper implements Closeable {
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

                producedModels.add(AidlModel.create(iInterface, methods));

                pendingElements.remove(qualified);
            } else if (roundEnvironment.processingOver()) {
                throw new ElementException(
                        "Looks like there are compilation errors in the @AIDL-annotated interface, please fix that.", erroneousElement);
            } else {
                pendingElements.add(qualified);

                return false;
            }
        }

        new StubGenerator(getBaseEnvironment(), producedModels).make(getFiler());

        new ProxyGenerator(getBaseEnvironment(), producedModels).make(getFiler());

        return true;
    }

    private List<TypeInvocation<ExecutableElement, ExecutableType>> gatherAidl2Methods(DeclaredType element) {
        final Collection<? extends ExecutableElement> allMethods =
                ElementFilter.methodsIn(elements.getAllMembers((TypeElement) element.asElement()));

        final List<TypeInvocation<ExecutableElement, ExecutableType>> aidl2Methods = new ArrayList<>(allMethods.size());

        for (ExecutableElement method : allMethods) {
            if (!Util.isAIDL2method(method)) continue;

            final ExecutableType methodType = (ExecutableType) types.asMemberOf(element, method);

            final TypeInvocation<ExecutableElement, ExecutableType> typeInvocation =
                    new TypeInvocation<>(method, methodType);

            if (isSubsignature(typeInvocation, asBinder)) continue;

            aidl2Methods.add(typeInvocation);
        }

        return aidl2Methods;
    }

    @Override
    public void close() {
    }
}
