package net.sf.aidl2.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;

public final class AidlModel {
    @NotNull
    final Annotation[] migrated;

    @NotNull
    final CharSequence serverImplName;

    @NotNull
    final CharSequence clientImplName;

    @NotNull
    final CharSequence descriptor;

    @NotNull
    final TypeName interfaceName;

    final int suppressed;

    final boolean insecure;

    final boolean assumeFinal;

    @NotNull
    final List<AidlMethodModel> methods;

    private AidlModel(@NotNull CharSequence serverImplName,
                     @NotNull CharSequence clientImplName,
                     @NotNull CharSequence descriptor,
                     @NotNull TypeName interfaceName,
                     @NotNull List<AidlMethodModel> methods,
                     int suppressed,
                     boolean insecure,
                     boolean assumeFinal,
                     @NotNull final Annotation... migrated) {
        this.serverImplName = serverImplName;
        this.clientImplName = clientImplName;
        this.descriptor = descriptor;
        this.interfaceName = interfaceName;
        this.insecure = insecure;
        this.assumeFinal = assumeFinal;
        this.methods = methods;
        this.migrated = migrated;
        this.suppressed = suppressed;
    }

    public static AidlModel create(TypeElement aidlInterface, Collection<TypeInvocation<ExecutableElement, ExecutableType>> allMethods) {
        final AIDL aidl = aidlInterface.getAnnotation(AIDL.class);

        final SuppressWarnings typeSw = aidlInterface.getAnnotation(SuppressWarnings.class);

        final boolean nullableOnType = aidl.defaultNullable();

        final int warningsSuppressedOnType = Util.getSuppressed(typeSw);

        final Annotation[] typeTransplanted = typeSw != null ? new Annotation[] { typeSw } : new Annotation[0];

        final List<AidlMethodModel> methods = new ArrayList<>(allMethods.size());

        final TypeName ifaceName = ClassName.get(aidlInterface);

        for (TypeInvocation<ExecutableElement, ExecutableType> methodEl : allMethods) {
            methods.add(AidlMethodModel.create(methodEl, nullableOnType, warningsSuppressedOnType));
        }

        CharSequence descriptor = aidl.value();

        if (descriptor == null || descriptor.length() == 0) {
            descriptor = ifaceName.toString();
        }

        final CharSequence serverImpl = aidlInterface.getSimpleName() + "$$AidlServerImpl";

        final CharSequence clientImpl = aidlInterface.getSimpleName() + "$$AidlClientImpl";

        return new AidlModel(
                serverImpl,
                clientImpl,
                descriptor,
                ifaceName,
                methods,
                warningsSuppressedOnType,
                aidl.insecure(),
                aidl.assumeFinal(),
                typeTransplanted);
    }
}