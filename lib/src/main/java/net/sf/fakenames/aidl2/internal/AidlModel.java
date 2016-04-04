package net.sf.fakenames.aidl2.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import net.sf.fakenames.aidl2.AIDL;
import net.sf.fakenames.aidl2.internal.codegen.MethodInstantiation;
import net.sf.fakenames.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.TypeElement;

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

    final boolean defaultNullable;

    final boolean unchecked;

    final boolean insecure;

    @NotNull
    final List<AidlMethodModel> methods;

    private AidlModel(@NotNull CharSequence serverImplName,
                     @NotNull CharSequence clientImplName,
                     @NotNull CharSequence descriptor,
                     @NotNull TypeName interfaceName,
                     @NotNull List<AidlMethodModel> methods,
                     boolean unchecked,
                     boolean insecure,
                     boolean defaultNullable,
                     @NotNull final Annotation... migrated) {
        this.serverImplName = serverImplName;
        this.clientImplName = clientImplName;
        this.descriptor = descriptor;
        this.interfaceName = interfaceName;
        this.insecure = insecure;
        this.methods = methods;
        this.defaultNullable = defaultNullable;
        this.migrated = migrated;
        this.unchecked = unchecked;
    }

    public static AidlModel create(TypeElement aidlInterface, Collection<MethodInstantiation> allMethods) {
        final AIDL aidl = aidlInterface.getAnnotation(AIDL.class);

        final SuppressWarnings typeSw = aidlInterface.getAnnotation(SuppressWarnings.class);

        final boolean nullableOnType = aidl.defaultNullable();

        final boolean suppressUncheckedOnType = typeSw != null && Util.isSuppressed("unchecked", typeSw);

        final Annotation[] typeTransplanted = typeSw != null ? new Annotation[] { typeSw } : new Annotation[0];

        final List<AidlMethodModel> methods = new ArrayList<>(allMethods.size());

        final TypeName ifaceName = ClassName.get(aidlInterface);

        for (MethodInstantiation methodEl : allMethods) {
            methods.add(AidlMethodModel.create(methodEl, nullableOnType, suppressUncheckedOnType));
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
                suppressUncheckedOnType,
                aidl.insecure(),
                aidl.defaultNullable(),
                typeTransplanted);
    }
}