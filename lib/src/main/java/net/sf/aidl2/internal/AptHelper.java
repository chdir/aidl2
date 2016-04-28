package net.sf.aidl2.internal;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.util.JavaVersion;
import net.sf.aidl2.internal.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.TypeKindVisitor6;
import javax.lang.model.util.Types;

import static net.sf.aidl2.internal.util.Util.isProperDeclared;
import static net.sf.aidl2.internal.util.Util.isTypeOf;
import static net.sf.aidl2.internal.util.Util.literal;

public abstract class AptHelper implements ProcessingEnvironment {
    private final AidlProcessor.Environment environment;

    protected final Elements elements;
    protected final Types types;

    protected final DeclaredType theObject;
    protected final DeclaredType voidClass;
    protected final DeclaredType theThrowable;
    protected final DeclaredType theException;
    protected final DeclaredType theRuntimeException;

    protected final DeclaredType theBinder;
    protected final DeclaredType theIInterface;
    protected final DeclaredType theCreator;

    protected final TypeMirror listBound;
    protected final TypeMirror setBound;

    public DeclaredType hashSet;
    public DeclaredType arrayList;

    protected final DeclaredType theCollection;

    protected final TypeElement collectionElement;

    protected final TypeInvocation<ExecutableElement, ExecutableType> onTransact;
    protected final TypeInvocation<ExecutableElement, ExecutableType> asBinder;

    public AptHelper(AidlProcessor.Environment environment) {
        this.environment = environment;

        this.elements = environment.getElementUtils();
        this.types = environment.getTypeUtils();

        theObject = lookup(Object.class);
        voidClass = lookup(Void.class);
        theThrowable = lookup(Throwable.class);
        theException = lookup(Exception.class);
        theRuntimeException = lookup(RuntimeException.class);

        this.hashSet = lookup(HashSet.class);
        this.arrayList = lookup(ArrayList.class);

        theBinder = lookup("android.os.Binder");
        theIInterface = lookup("android.os.IInterface");
        theCreator = lookup("android.os.Parcelable.Creator");

        collectionElement = elements.getTypeElement(Collection.class.getName());
        theCollection = types.getDeclaredType(collectionElement);

        onTransact = lookupMethod(theBinder, "onTransact", boolean.class, int.class, "Parcel", "Parcel", int.class);
        asBinder = lookupMethod(theIInterface, "asBinder", "IBinder");

        listBound = types.getWildcardType(null, arrayList);
        setBound = types.getWildcardType(null, hashSet);
    }

    public boolean isSubsignature(TypeInvocation<?, ExecutableType> m1, TypeInvocation<?, ExecutableType> m2) {
        return m1.element.getSimpleName().equals(m2.element.getSimpleName())
                && types.isSubsignature(m1.type, m2.type);
    }

    protected boolean isSuppressed(int suppressed, int suppressedMask) {
        return environment.getConfig().isNoWarn()
                || ((suppressed & suppressedMask) != 0);
    }

    public CodeBlock emitCasts(TypeMirror t, TypeMirror t2, CodeBlock input) {
        // It is hard and sometimes not even possible to tell if intersection cast is needed,
        // much less to handle those casts. This should do for Java versions >= 8 and avoid
        // redundant casts when no intersection types are involved
        if (isTricky(t) || isTricky(t2)) {
            return literal("$T.unsafeCast($L)", ClassName.get(AidlUtil.class), input);
        }

        if (types.isAssignable(t, t2)) {
            // no need for casting
            return input;
        } else {
            final TypeMirror captured = captureAll(t2);

            if (types.isAssignable(captured, t2)) {
                // There is still a possibility, that javac refuses to perform a cast
                // (e.g. between collections of incompatible generic types).
                if (castAllowed(t, captured)) {
                    return literal("($T) $L", captured, input);
                }
            }

            final TypeMirror erased = types.erasure(t2);

            // Emit simple unchecked cast. This can happen only when incompatible type are used, so
            // receiving a warning will be justified
            if (types.isAssignable(erased, t2)) {
                return literal("($T) $L", erased, input);
            }

            // emit cast via the helper method
            // TODO: warn user
            return literal("$T.unsafeCast($L)", ClassName.get(AidlUtil.class), input);
        }
    }

    private boolean isTricky(TypeMirror t) {
        if (t == null) return false;

        final TypeKind kind = t.getKind();

        if (kind.isPrimitive()) return false;

        switch (kind) {
            case DECLARED:
                return !isProperDeclared(t);
            case ARRAY:
                return false;
            default:
                final TypeMirror simplified = types.capture(t);

                return simplified.getKind() != TypeKind.DECLARED || !isProperDeclared(simplified);
        }
    }

    public boolean castAllowed(TypeMirror Source, TypeMirror Target) {
        // according to Java type system wildcards clearly belong in upper plane of existence,
        // furthermore letting them appear here is a *totally* unexpected condition
        TypeMirror invalid = null;

        if (Source.getKind() == TypeKind.WILDCARD) {
            invalid = Source;
        } else if (Target.getKind() == TypeKind.WILDCARD) {
            invalid = Target;
        }

        if (invalid != null) {
            getBaseEnvironment().getLogger().log(
                    "Wildcard type " + invalid + " is not supposed to appear in neither top-level, " +
                    "nor element-level casting context. Consider reporting a bug.");

            return false;
        }

        // do not proceed with analysis, let the (potentially failed) cast be generated instead
        if (Util.isProperClass(Source) && Util.isProperClass(Target)) {
            return true;
        }

        // freaking type arguments weren't supposed to get this far!
        if (!isProperDeclared(Source) || !isProperDeclared(Target)) {
            return false;
        }

        final DeclaredType S = (DeclaredType) Source;
        final DeclaredType T = (DeclaredType) Target;

        if (Util.isFinal(S.asElement())) {
            return true;
        }

        final Collection<? extends TypeMirror> parentsOfS = getSupertypes(S);
        final Collection<? extends TypeMirror> parentsOfT = getSupertypes(T);

        for (TypeMirror parentOfS : parentsOfS) {
            if (!isProperDeclared(parentOfS)) {
                // TODO: WHAT?! maybe a system for issuing warnings in cases like this should be made...
                continue;
            }

            final TypeMirror erased = types.erasure(parentOfS);

            for (TypeMirror parentOfT : parentsOfT) {
                if (types.isSameType(erased, types.erasure(parentOfT))) {
                    if (!isProperDeclared(parentOfT)) {
                        continue;
                    }

                    if (distinct((DeclaredType) parentOfT, (DeclaredType) parentOfS)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public Collection<? extends TypeMirror> getSupertypes(DeclaredType s) {
        final Set<TypeMirror> superTypes = new HashSet<>(3);

        new SimpleTypeVisitor6<Void, Void>() {
            @Override
            public Void visitUnknown(TypeMirror typeMirror, Void unused) {
                return defaultAction(typeMirror, unused);
            }

            @Override
            protected Void defaultAction(TypeMirror typeMirror, Void unused) {
                superTypes.add(typeMirror);

                for (TypeMirror parent : (types.directSupertypes(typeMirror))) {
                    if (!types.isSameType(parent, typeMirror)) {
                        visit(parent);
                    }
                }

                return null;
            }
        }.visit(s);

        return superTypes;
    }

    public boolean distinct(DeclaredType t1, DeclaredType t2) {
        final Name n1 = Util.getQualifiedName(t1);

        assert n1 != null;

        if (!n1.equals(Util.getQualifiedName(t2))) {
            return true;
        }

        final List<? extends TypeMirror> t1els = t1.getTypeArguments();
        final List<? extends TypeMirror> t2els = t1.getTypeArguments();

        for (int i = 0; i < t1els.size(); ++i) {
            if (!typeArgsDistinct(t1els.get(i), t2els.get(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean typeArgsDistinct(TypeMirror targ1, TypeMirror targ2) {
        final TypeKind type1Kind = targ1.getKind();
        final TypeKind type2Kind = targ2.getKind();

        switch (type1Kind) {
            case WILDCARD:
            case TYPEVAR:
                break;
            default:
                if (type2Kind != TypeKind.WILDCARD && type2Kind != TypeKind.TYPEVAR) {
                    return !types.isSameType(targ1, targ2);
                }
        }

        final TypeMirror t1erasure = types.erasure(targ1);
        final TypeMirror t2erasure = types.erasure(targ2);

        return !types.isSubtype(t1erasure, t2erasure) && !types.isSubtype(t2erasure, t1erasure);
    }

    public CodeBlock emitFullCast(TypeMirror t, TypeMirror t2, CodeBlock input) {
        if (types.isAssignable(t, t2)) {
            // no need for casting
            return input;
        } else {
            final TypeMirror captured = captureAll(t2);

            if (types.isAssignable(captured, t2)) {
                // emit a simple unchecked cast
                return literal("(($T) $L)", captured, input);
            } else {
                // emit cast via the helper method
                // TODO: warn user
                return literal("($T.unsafeCast($L))", ClassName.get(AidlUtil.class), input);
            }
        }
    }

    private final TypeVisitor<DeclaredType, TypeMirror> BOUND_REFINER = new SharedParentRefiner();

    private final TypeVisitor<TypeMirror, TypeMirror> NESTED_BOUND_REFINER = new NestedTypeArgRefiner();

    public TypeMirror gaugeConcreteParent(TypeMirror mirror, TypeMirror returnedType) {
        final TypeKind targetKind = mirror.getKind();

        switch (targetKind) {
            case ARRAY:
                return types.erasure(mirror);
            default:
                if (targetKind.isPrimitive()) {
                    return mirror;
                }
        }

        final DeclaredType foundParent = BOUND_REFINER.visit(mirror, returnedType);

        if (foundParent == null) {
            return theObject;
        }

        return types.getDeclaredType((TypeElement) foundParent.asElement(), substituteAllArgs(foundParent));
    }

    private TypeMirror[] substituteAllArgs(DeclaredType foundParent) {
        final List<? extends TypeMirror> typeArgs = foundParent.getTypeArguments();

        final TypeMirror[] result = new TypeMirror[typeArgs.size()];

        for (int i = 0; i < typeArgs.size(); ++i) {
            final TypeMirror refined = NESTED_BOUND_REFINER.visit(typeArgs.get(i), theObject);

            result[i] = refined == null ? theObject : refined;
        }

        return result;
    }

    public MethodSpec.Builder override(ExecutableElement method, DeclaredType enclosing) {
        final List<? extends VariableElement> parameters = method.getParameters();

        final ExecutableType executableType = (ExecutableType) types.asMemberOf(enclosing, method);

        final List<? extends TypeMirror> resolvedParameterTypes = executableType.getParameterTypes();

        final TypeMirror resolvedReturnType = executableType.getReturnType();

        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .returns(TypeName.get(resolvedReturnType))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .varargs(method.isVarArgs());

        for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
            TypeVariable var = (TypeVariable) typeParameterElement.asType();
            methodBuilder.addTypeVariable(TypeVariableName.get(var));
        }

        for (AnnotationMirror mirror : method.getAnnotationMirrors()) {
            if (isTypeOf(Override.class, mirror.getAnnotationType())) {
                continue;
            }

            methodBuilder.addAnnotation(AnnotationSpec.get(mirror));
        }

        for (int i = 0; i < parameters.size(); i++) {
            final VariableElement param = parameters.get(i);

            final TypeName type = TypeName.get(resolvedParameterTypes.get(i));

            final Set<Modifier> modifiers = param.getModifiers();

            final ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(type, param.getSimpleName().toString())
                    .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

            for (AnnotationMirror mirror : param.getAnnotationMirrors()) {
                parameterBuilder.addAnnotation(AnnotationSpec.get(mirror));
            }

            methodBuilder.addParameter(parameterBuilder.build());
        }

        for (TypeMirror thrownType : executableType.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(thrownType));
        }

        return methodBuilder;
    }

    public VariableElement lookupStaticField(DeclaredType type, CharSequence fieldName, TypeMirror fieldType) {
        for (VariableElement field : ElementFilter.fieldsIn(type.asElement().getEnclosedElements())) {
            final Collection<? extends Modifier> modifiers = field.getModifiers();

            if (!modifiers.contains(Modifier.STATIC) || !modifiers.contains(Modifier.PUBLIC)) {
                continue;
            }

            if (!field.getSimpleName().contentEquals(fieldName)) {
                continue;
            }

            if (!types.isAssignable(field.asType(), fieldType)) {
                continue;
            }

            return field;
        }

        return null;
    }

    public TypeElement lookupStaticClass(DeclaredType type, CharSequence className, TypeMirror classType) {
        for (TypeElement clazz : ElementFilter.typesIn(type.asElement().getEnclosedElements())) {
            final Collection<? extends Modifier> modifiers = clazz.getModifiers();

            if (clazz.getNestingKind() != NestingKind.MEMBER
                    || !modifiers.contains(Modifier.STATIC)
                    || !modifiers.contains(Modifier.PUBLIC)
                    || !clazz.getKind().isClass()) {
                continue;
            }

            if (!clazz.getSimpleName().contentEquals(className)) {
                continue;
            }

            if (!types.isAssignable(clazz.asType(), classType)) {
                continue;
            }

            return clazz;
        }

        return null;
    }

    public boolean isEffectivelyObject(TypeMirror type) {
        final TypeMirror captured = captureAll(type);

        if (!isTricky(captured)) {
            return isTypeOf(Object.class, captured);
        }

        return types.isSameType(types.erasure(type), theObject);
    }

    protected boolean hasBound(TypeMirror mirror, TypeMirror bound) {
        return types.contains(bound, types.erasure(mirror));
    }

    public TypeInvocation<ExecutableElement, ExecutableType> lookupMethod(DeclaredType type, CharSequence methodName, Object ret, Object... argv) {
        methodSearch:
        for (ExecutableElement method : ElementFilter.methodsIn(elements.getAllMembers((TypeElement) type.asElement()))) {
            final Collection<? extends Modifier> modifiers = method.getModifiers();

            if (!method.getSimpleName().contentEquals(methodName)
                    || modifiers.contains(Modifier.PRIVATE)
                    || modifiers.contains(Modifier.STATIC)) {
                continue;
            }

            final ExecutableType methodType = (ExecutableType) types.asMemberOf(type, method);

            if (methodType.getReturnType().getKind() != TypeKind.VOID) {
                if (!Util.matches(ret, types.erasure(methodType.getReturnType()))) {
                    continue;
                }
            }

            if (argv.length == 0) {
                if (!method.getParameters().isEmpty()) {
                    continue;
                }
            } else {
                final List<? extends TypeMirror> args = methodType.getParameterTypes();

                if (args.size() != argv.length) {
                    continue;
                }

                for (int i = 0; i < argv.length; i++) {
                    if (!Util.matches(argv[i], types.erasure(args.get(i)))) {
                        continue methodSearch;
                    }
                }
            }

            return new TypeInvocation<>(method, methodType);
        }

        throw new IllegalArgumentException("Failed to find instance method \"" + methodName + "\" in " + type);
    }

    public ExecutableElement lookupStaticMethod(DeclaredType type, CharSequence methodName, TypeMirror ret, Object... argv) {
        methodSearch:
        for (ExecutableElement method : ElementFilter.methodsIn(type.asElement().getEnclosedElements())) {
            final Collection<? extends Modifier> modifiers = method.getModifiers();

            if (!method.getSimpleName().contentEquals(methodName)
                    || !modifiers.contains(Modifier.PUBLIC)
                    || !modifiers.contains(Modifier.STATIC)) {
                continue;
            }

            final ExecutableType methodType = (ExecutableType) types.asMemberOf(type, method);

            if (types.isAssignable(ret, methodType.getReturnType())) {
                continue;
            }

            if (argv.length == 0) {
                if (!method.getParameters().isEmpty()) {
                    continue;
                }
            } else {
                final List<? extends TypeMirror> args = methodType.getParameterTypes();

                if (args.size() != argv.length) {
                    continue;
                }

                for (int i = 0; i < argv.length; i++) {
                    if (!Util.matches(argv[i], types.erasure(args.get(i)))) {
                        continue methodSearch;
                    }
                }
            }

            return method;
        }

        return null;
    }

    protected DeclaredType getCollectionInterface(TypeMirror type) {
        final ArrayList<DeclaredType> parents = new ArrayList<>(1);

        final CollectionTypeRefiner refiner = new CollectionTypeRefiner(parents);

        refiner.visit(type);

        if (parents.isEmpty()) {
            throw new IllegalStateException("The type " + type + " appears to be subtype of Collection, but the exact type parameter can not be determined");
        }

        Collections.sort(parents, (left, right) -> {
            if (types.isSameType(left, right)) {
                return 0;
            }

            boolean leftIsSubtypeOfRight = types.isAssignable(left, right);

            boolean rightIsSubtypeOfLeft = types.isAssignable(right, left);

            // move least concrete types towards the end of list
            if (leftIsSubtypeOfRight) {
                if (!rightIsSubtypeOfLeft) {
                    return +1;
                }
            } else {
                if (rightIsSubtypeOfLeft) {
                    return -1;
                }
            }

            // move raw types towards end of list
            if (isRaw(left)) {
                return +1;
            }

            return 0;
        });

        return parents.get(0);
    }

    public boolean isChecked(TypeMirror throwable) {
        return !types.isSubtype(throwable, theRuntimeException) && types.isSubtype(throwable, theException);
    }

    public boolean isRaw(TypeMirror type) {
        return types.isSameType(types.erasure(type), type);
    }

    protected DeclaredType lookup(Class<?> clazz) {
        return lookup(clazz.getName());
    }

    protected DeclaredType lookup(CharSequence name) {
        return types.getDeclaredType(elements.getTypeElement(name));
    }

    public TypeElement lookupGeneric(String s) {
        return elements.getTypeElement(s);
    }

    private class DeclaredTypeRefiner extends TypeKindVisitor6<DeclaredType, TypeMirror> {
        @Override
        public DeclaredType visitDeclared(DeclaredType type, TypeMirror desirableParent) {
            if (types.isSubtype(type, desirableParent)) {
                final Element element = types.asElement(type);

                if (element != null) {
                    final ElementKind elKind = element.getKind();

                    if (elKind.isClass() || elKind.isInterface()) {
                        return type;
                    }
                }

                return defaultAction(type, desirableParent);
            }

            return null;
        }

        @Override
        public DeclaredType visitUnknown(TypeMirror typeMirror, TypeMirror typeMirror2) {
            return defaultAction(typeMirror, typeMirror2);
        }

        @Override
        protected DeclaredType defaultAction(TypeMirror type, TypeMirror desirableParent) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            final List<? extends TypeMirror> superTypes = types.directSupertypes(type);

            for (TypeMirror parent : superTypes) {
                final DeclaredType discovered = visit(parent, desirableParent);

                if (discovered != null) {
                    return discovered;
                }
            }

            return null;
        }
    }

    private final class NestedTypeArgRefiner extends TypeKindVisitor6<TypeMirror, TypeMirror> {
        @Override
        public TypeMirror visitDeclared(DeclaredType type, TypeMirror desirableParent) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            if (types.isAssignable(type, desirableParent)) {
                final Element element = types.asElement(type);

                if (element != null) {
                    final ElementKind elKind = element.getKind();

                    if (elKind.isClass() || elKind.isInterface()) {
                        return type;
                    }
                }
            }

            return defaultAction(type, desirableParent);
        }

        @Override
        public TypeMirror visitWildcard(WildcardType wildcardType, TypeMirror desirableParent) {
            TypeMirror extendsBound = wildcardType.getExtendsBound();

            if (extendsBound != null) {
                extendsBound = visit(extendsBound, desirableParent);
            }

            TypeMirror superBound = wildcardType.getSuperBound();

            if (superBound != null) {
                superBound = visit(superBound, desirableParent);
            }

            return types.getWildcardType(extendsBound, superBound);
        }

        @Override
        public TypeMirror visitTypeVariable(TypeVariable typeVariable, TypeMirror typeMirror) {
            TypeMirror upperBound = typeVariable.getUpperBound();

            if (upperBound != null) {
                upperBound = visit(upperBound, typeMirror);

                if (upperBound != null) {
                    return types.getWildcardType(upperBound, null);
                }
            }

            TypeMirror lowerBound = typeVariable.getLowerBound();

            if (lowerBound != null) {
                lowerBound = visit(lowerBound, typeMirror);

                if (lowerBound != null) {
                    return types.getWildcardType(null, lowerBound);
                }
            }

            return types.getWildcardType(null, null);
        }

        @Override
        public TypeMirror visitUnknown(TypeMirror typeMirror, TypeMirror typeMirror2) {
            return defaultAction(typeMirror, typeMirror2);
        }

        @Override
        protected TypeMirror defaultAction(TypeMirror type, TypeMirror desirableParent) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            final List<? extends TypeMirror> superTypes = types.directSupertypes(type);

            for (TypeMirror parent : superTypes) {
                final TypeMirror discovered = visit(parent, desirableParent);

                if (discovered != null) {
                    return discovered;
                }
            }

            return null;
        }
    }

    private final class SharedParentRefiner extends DeclaredTypeRefiner {
        @Override
        public DeclaredType visitDeclared(DeclaredType type, TypeMirror desirableParent) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            if (types.isAssignable(desirableParent, type)) {
                final Element element = types.asElement(type);

                if (element != null) {
                    final ElementKind elKind = element.getKind();

                    if (elKind.isClass() || elKind.isInterface()) {
                        return type;
                    }
                }
            }

            return defaultAction(type, desirableParent);
        }

        @Override
        protected DeclaredType defaultAction(TypeMirror type, TypeMirror desirableParent) {
            if (isEffectivelyObject(type)) {
                return null;
            }

            final List<? extends TypeMirror> superTypes = types.directSupertypes(type);

            for (TypeMirror parent : superTypes) {
                final DeclaredType discovered = visit(parent, desirableParent);

                if (discovered != null) {
                    return discovered;
                }
            }

            return null;
        }
    }

    private final class CollectionTypeRefiner extends TypeKindVisitor6<DeclaredType, TypeMirror> {
        private final Collection<DeclaredType> input;

        private CollectionTypeRefiner(Collection<DeclaredType> input) {
            this.input = input;
        }

        @Override
        public DeclaredType visitDeclared(DeclaredType type, TypeMirror ignored) {
            if (isProperDeclared(type) && types.isSubtype(type, theCollection)) {
                input.add(type);

                return null;
            } else {
                return defaultAction(type, theCollection);
            }
        }

        @Override
        public DeclaredType visitUnknown(TypeMirror typeMirror, TypeMirror typeMirror2) {
            return defaultAction(typeMirror, typeMirror2);
        }

        @Override
        protected DeclaredType defaultAction(TypeMirror type, TypeMirror desirableParent) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            final List<? extends TypeMirror> superTypes = types.directSupertypes(type);

            for (TypeMirror parent : superTypes) {
                visit(parent, desirableParent);
            }

            return null;
        }
    }

    private final class ConcreteTypeRefiner extends DeclaredTypeRefiner {
        @Override
        public DeclaredType visitDeclared(DeclaredType type, TypeMirror desirableParent) {
            if (types.isSubtype(type, desirableParent)) {
                final Element element = types.asElement(type);

                if (element != null && element.getKind().isClass() && !element.getModifiers().contains(Modifier.ABSTRACT)) {
                    return type;
                }

                return defaultAction(type, desirableParent);
            }

            return null;
        }
    }

    private final TypeVisitor<DeclaredType, TypeMirror> CONCRETE_REFINER = new ConcreteTypeRefiner();

    private final TypeVisitor<DeclaredType, TypeMirror> DECLARED_REFINER = new DeclaredTypeRefiner();

    protected DeclaredType findConcreteParent(TypeMirror type, DeclaredType asType) {
        return CONCRETE_REFINER.visit(type, asType);
    }

    protected DeclaredType findDeclaredParent(TypeMirror type, DeclaredType asType) {
        return DECLARED_REFINER.visit(type, asType);
    }

    protected boolean isVoid(TypeMirror returnType) {
        switch (returnType.getKind()) {
            case VOID:
                return true;
            default:
                return types.isSameType(returnType, voidClass);
        }
    }

    public CodeBlock emitCapacityConstructorCall(TypeMirror capturedType, Object sizeLiteral) {
        final TypeMirror erased = types.erasure(capturedType);

        if (JavaVersion.atLeast(JavaVersion.JAVA_1_7)) {
            if (Util.isProperDeclared(capturedType)) {
                Collection<? extends TypeMirror> args = ((DeclaredType) capturedType).getTypeArguments();

                if (!args.isEmpty()) {
                    return literal("new $T<>($L)", erased, sizeLiteral);
                }
            }
        }

        return literal("new $T($L)", erased, sizeLiteral);
    }

    public CodeBlock emitDefaultConstructorCall(TypeMirror capturedType) {
        final TypeMirror erased = types.erasure(capturedType);

        if (JavaVersion.atLeast(JavaVersion.JAVA_1_7)) {
            if (Util.isProperDeclared(capturedType)) {
                Collection<? extends TypeMirror> args = ((DeclaredType) capturedType).getTypeArguments();

                if (!args.isEmpty()) {
                    return literal("new $T<>()", erased);
                }
            }
        }

        return literal("new $T()", erased);
    }

    /**
     * Transform a type by recursively applying capture conversion to itself and all types arguments.
     * Types, that can not be converted to declared types by capture alone (e.g. type intersections)
     * are subjected to erasure.
     *
     * @param type concrete (non-abstract) declared type
     * @return transformation of {@code type}
     */
    public TypeMirror captureAll(TypeMirror type) {
        if (type == null) return null;

        if (type.getKind().isPrimitive()) {
            return type;
        }

        return keepIfSameType(type, captureInner(type, new ArrayList<>()));
    }

    // Check if a is the same as b from type system's viewpoint.
    // If so, return a, else b.
    private <T extends TypeMirror> T keepIfSameType(T a, T b) {
        return types.isSameType(a, b) ? a : b;
    }

    private TypeMirror captureInner(TypeMirror type, List<TypeMirror> encountered) {
        final TypeKind kind = type.getKind();

        switch (kind) {
            case ARRAY:
                return types.erasure(type);
            default:
                final TypeMirror refined = types.capture(type);

                if (Util.isProperDeclared(refined)) {
                    return captureAllArgs((DeclaredType) refined, encountered);
                }

                final TypeMirror erasedArg = types.erasure(type);

                if (!Util.isProperDeclared(erasedArg)) {
                    return theObject;
                }

                final TypeMirror declaredParent = findDeclaredParent(type, (DeclaredType) erasedArg);

                if (declaredParent == null) {
                    return erasedArg;
                }

                final TypeMirror refinedParent = types.capture(declaredParent);

                if (Util.isProperDeclared(refinedParent)) {
                    return captureAllArgs((DeclaredType) refinedParent, encountered);
                }

                return erasedArg;
        }
    }

    private DeclaredType captureAllArgs(DeclaredType type, List<TypeMirror> encountered) {
        final TypeMirror erased = types.erasure(type);

        for (TypeMirror encounteredType : encountered) {
            if (types.isSameType(erased, encounteredType)) {
                return Util.isProperDeclared(erased) ? (DeclaredType) erased : theObject;
            }
        }

        encountered.add(erased);

        final List<? extends TypeMirror> args = type.getTypeArguments();

        if (args.isEmpty()) {
            return type;
        }

        final TypeMirror[] argumentCaptures = new TypeMirror[args.size()];

        for (int i = 0; i < args.size(); i++) {
            // clone the encountered types list to
            argumentCaptures[i] = captureInner(args.get(i), new ArrayList<>(encountered));
        }

        return types.getDeclaredType((TypeElement) type.asElement(), argumentCaptures);
    }

    public AidlProcessor.Environment getBaseEnvironment() {
        return environment;
    }

    public ProcessingEnvironment getSystemEnvironment() {
        return environment.getBaseEnvironment();
    }

    @Override
    public Map<String, String> getOptions() {
        return environment.getOptions();
    }

    @Override
    public Messager getMessager() {
        return environment.getMessager();
    }

    @Override
    public Filer getFiler() {
        return environment.getFiler();
    }

    @Override
    public Elements getElementUtils() {
        return null;
    }

    @Override
    public Types getTypeUtils() {
        return environment.getTypeUtils();
    }

    @Override
    public SourceVersion getSourceVersion() {
        return environment.getSourceVersion();
    }

    @Override
    public Locale getLocale() {
        return environment.getLocale();
    }
}
