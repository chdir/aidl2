package net.sf.aidl2.internal;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.internal.exceptions.AnnotationValueException;
import net.sf.aidl2.internal.exceptions.QuietException;
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
import static javax.tools.Diagnostic.Kind.WARNING;

final class Session extends AptHelper implements Closeable {
    private Set<Name> pendingElements = new HashSet<>();

    public Session(net.sf.aidl2.internal.AidlProcessor.Environment environment) {
        super(environment);
    }

    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment)
            throws net.sf.aidl2.internal.exceptions.ElementException, net.sf.aidl2.internal.exceptions.AnnotationException, AnnotationValueException, IOException {
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
                final List<net.sf.aidl2.internal.codegen.MethodInstantiation> methods = gatherAidl2Methods(interfaceType);

                final net.sf.aidl2.internal.AidlMethodValidator methodValidator =
                        new net.sf.aidl2.internal.AidlMethodValidator(getBaseEnvironment(), (DeclaredType) iInterface.asType());

                for (net.sf.aidl2.internal.codegen.MethodInstantiation methodEl : methods) {
                    methodValidator.validate(methodEl);
                }

                producedModels.add(AidlModel.create(iInterface, methods));

                pendingElements.remove(qualified);
            } else if (roundEnvironment.processingOver()) {
                throw new net.sf.aidl2.internal.exceptions.ElementException(
                        "Looks like there are compiler errors in the @AIDL-annotated interface, please fix that.", erroneousElement);
            } else {
                pendingElements.add(qualified);

                return false;
            }
        }

        new net.sf.aidl2.internal.StubGenerator(getBaseEnvironment(), producedModels).make(getFiler());

        new ProxyGenerator(getBaseEnvironment(), producedModels).make(getFiler());

        return true;
    }

    private List<net.sf.aidl2.internal.codegen.MethodInstantiation> gatherAidl2Methods(DeclaredType element) {
        final Collection<? extends ExecutableElement> allMethods =
                ElementFilter.methodsIn(elements.getAllMembers((TypeElement) element.asElement()));

        final List<net.sf.aidl2.internal.codegen.MethodInstantiation> aidl2Methods = new ArrayList<>(allMethods.size());

        for (ExecutableElement method : allMethods) {
            if (!Util.isAIDL2method(method)) continue;

            final ExecutableType methodType = (ExecutableType) types.asMemberOf(element, method);

            final net.sf.aidl2.internal.codegen.MethodInstantiation methodInstantiation = new net.sf.aidl2.internal.codegen.MethodInstantiation(method, methodType);

            if (isSubsignature(methodInstantiation, asBinder)) continue;

            aidl2Methods.add(methodInstantiation);
        }

        return aidl2Methods;
    }

    @Override
    public void close() {
    }
}
