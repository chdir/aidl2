package net.sf.aidl2.internal;

import android.os.IBinder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import net.sf.aidl2.AIDL;
import net.sf.aidl2.Call;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.exceptions.AnnotationException;
import net.sf.aidl2.internal.util.Util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.lang.model.element.*;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class AidlModel {
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
    final Map<Integer, AidlMethodModel> methods;

    final ContractHasher digest = ContractHasher.create();

    // should really be MutableLong, but that's also fine
    final AtomicLong rpcVersionId = new AtomicLong();

    private AidlModel(@NotNull CharSequence serverImplName,
                     @NotNull CharSequence clientImplName,
                     @NotNull CharSequence descriptor,
                     @NotNull TypeName interfaceName,
                     @NotNull Map<Integer, AidlMethodModel> methods,
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

    public static AidlModel create(TypeElement aidlInterface,
                                   List<TypeInvocation<ExecutableElement, ExecutableType>> allMethods,
                                   Comparator<AidlMethodModel> methodComparator) throws AnnotationException {
        final AIDL aidl = aidlInterface.getAnnotation(AIDL.class);

        final SuppressWarnings typeSw = aidlInterface.getAnnotation(SuppressWarnings.class);

        final boolean nullableOnType = aidl.defaultNullable();

        final int warningsSuppressedOnType = Util.getSuppressed(typeSw);

        final Annotation[] typeTransplanted = typeSw != null ? new Annotation[] { typeSw } : new Annotation[0];

        final ArrayList<AidlMethodModel> methods = new ArrayList<>(allMethods.size());

        final TypeName ifaceName = ClassName.get(aidlInterface);

        final List<? extends Element> topLevelElements = aidlInterface.getEnclosedElements();

        final NavigableMap<Integer, AidlMethodModel> assignedIds = new TreeMap<>();

        for (TypeInvocation<ExecutableElement, ExecutableType> methodEl : allMethods) {
            final int idxInFile = topLevelElements.indexOf(methodEl.element);

            AidlMethodModel aidlMethod = AidlMethodModel.create(methodEl, idxInFile, nullableOnType, warningsSuppressedOnType);

            if (aidlMethod.transactionId != -1) {
                final AidlMethodModel existing = assignedIds.put(aidlMethod.transactionId, aidlMethod);

                if (existing != null) {
                    AnnotationMirror a = Util.getAnnotation(methodEl.element, Call.class);

                    throw new AnnotationException("Duplicate transaction id: " + aidlMethod.transactionId, existing.element.element, a);
                }
            } else {
                methods.add(aidlMethod);
            }
        }

        Map<Integer, AidlMethodModel> sortedMethods;

        if (methods.size() != 0) {
            methods.sort(methodComparator);

            if (assignedIds.isEmpty()) {
                sortedMethods = assignIds(methods);
            } else {
                sortedMethods = assignIds(methods, assignedIds);
            }
        } else {
            sortedMethods = assignedIds;
        }

        CharSequence descriptor = aidl.value();

        if (descriptor.length() == 0) {
            descriptor = ifaceName.toString();
        }

        final CharSequence serverImpl = aidlInterface.getSimpleName() + "$$AidlServerImpl";

        final CharSequence clientImpl = aidlInterface.getSimpleName() + "$$AidlClientImpl";

        return new AidlModel(
                serverImpl,
                clientImpl,
                descriptor,
                ifaceName,
                sortedMethods,
                warningsSuppressedOnType,
                aidl.insecure(),
                aidl.assumeFinal(),
                typeTransplanted);
    }

    private static Map<Integer, AidlMethodModel> assignIds(ArrayList<AidlMethodModel> methods) {
        final Map<Integer, AidlMethodModel> result = new LinkedHashMap<>();

        int nextAvailableId = IBinder.FIRST_CALL_TRANSACTION - 1; int i;

        for (i = 0; i < methods.size(); ++i) {
            AidlMethodModel nextModel = methods.get(i);

            if (nextModel.idxInFile == -1) {
                break;
            }

            nextModel.transactionId = ++nextAvailableId;

            result.put(nextAvailableId, nextModel);
        }

        nextAvailableId = 9000;

        for (; i < methods.size(); ++i) {
            AidlMethodModel nextModel = methods.get(i);

            nextModel.transactionId = ++nextAvailableId;

            result.put(nextAvailableId, nextModel);
        }

        return result;
    }

    private static Map<Integer, AidlMethodModel> assignIds(ArrayList<AidlMethodModel> methods, NavigableMap<Integer, AidlMethodModel> assignedIds) {
        int availableKey = IBinder.FIRST_CALL_TRANSACTION - 1; int nextReservedKey;

        Integer nextReserved = assignedIds.higherKey(availableKey);
        nextReservedKey = nextReserved != null ? nextReserved : -1;

        int i;

        for (i = 0; i < methods.size(); ++i) {
            AidlMethodModel nextModel = methods.get(i);

            if (nextModel.idxInFile == -1) {
                break;
            }

            while (++availableKey == nextReservedKey) {
                nextReserved = assignedIds.higherKey(availableKey);
                nextReservedKey = nextReserved != null ? nextReserved : -1;
            }

            nextModel.transactionId = availableKey;

            assignedIds.put(availableKey, nextModel);
        }

        availableKey = 9000;

        nextReserved = assignedIds.higherKey(availableKey);
        nextReservedKey = nextReserved != null ? nextReserved : -1;

        for (; i < methods.size(); ++i) {
            AidlMethodModel nextModel = methods.get(i);

            while (++availableKey == nextReservedKey) {
                nextReserved = assignedIds.higherKey(availableKey);
                nextReservedKey = nextReserved != null ? nextReserved : -1;
            }

            nextModel.transactionId = availableKey;

            assignedIds.put(nextModel.transactionId, nextModel);
        }

        return assignedIds;
    }

    public void finishGeneration() throws IOException {
        rpcVersionId.set(digest.computeDigest());
    }
}