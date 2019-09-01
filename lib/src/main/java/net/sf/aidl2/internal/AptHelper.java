package net.sf.aidl2.internal;

import com.squareup.javapoet.*;

import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.util.CowCloneableList;
import net.sf.aidl2.internal.util.JavaVersion;
import net.sf.aidl2.internal.util.Util;

import java.util.*;

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
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.TypeKindVisitor6;
import javax.lang.model.util.Types;

import static net.sf.aidl2.internal.util.Util.*;

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
    protected final TypeMirror mapBound;

    public DeclaredType hashSet;
    public DeclaredType arrayList;
    public DeclaredType hashMap;

    protected final DeclaredType theCollection;
    protected final DeclaredType theMap;
    protected final DeclaredType theEntry;

    protected final TypeElement collectionElement;
    protected final TypeElement mapElement;
    protected final TypeElement entryElement;

    protected final TypeInvocation<ExecutableElement, ExecutableType> onTransact;
    protected final TypeInvocation<ExecutableElement, ExecutableType> asBinder;

    private final Comparator<? super TypeMirror> specificityComparator;

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
        this.hashMap = lookup(HashMap.class);

        theBinder = lookup("android.os.Binder");
        theIInterface = lookup("android.os.IInterface");
        theCreator = lookup("android.os.Parcelable.Creator");

        collectionElement = elements.getTypeElement(Collection.class.getName());
        theCollection = types.getDeclaredType(collectionElement);
        mapElement = elements.getTypeElement(Map.class.getName());
        theMap = types.getDeclaredType(mapElement);

        entryElement = elements.getTypeElement(Map.Entry.class.getCanonicalName());
        theEntry = types.getDeclaredType(entryElement);

        onTransact = lookupMethod(theBinder, "onTransact", boolean.class, int.class, "Parcel", "Parcel", int.class);
        asBinder = lookupMethod(theIInterface, "asBinder", "IBinder");

        listBound = types.getWildcardType(null, arrayList);
        setBound = types.getWildcardType(null, hashSet);
        mapBound = types.getWildcardType(null, hashMap);

        specificityComparator = (left, right) -> {
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

            boolean leftIsRaw = !hasTypeArgs(left);

            boolean rightIsRaw = !hasTypeArgs(right);

            // move raw types towards end of list
            if (leftIsRaw) {
                if (!rightIsRaw) {
                    return +1;
                }
            } else {
                if (rightIsRaw) {
                    return -1;
                }
            }

            return 0;
        };
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
            return literal("$T.unsafeCast($L)", AidlUtil.class, input);
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
            return literal("$T.unsafeCast($L)", AidlUtil.class, input);
        }
    }

    public CodeBlock emitFullCast(TypeMirror t, TypeMirror t2, CodeBlock input) {
        final CodeBlock cast = emitCasts(t, t2, input);

        if (cast == input) {
            return cast;
        }

        return literal("($L)", cast);
    }

    public boolean isTricky(TypeMirror t) {
        if (t == null) return false;

        final TypeKind k = t.getKind();

        switch (k) {
            case ARRAY:
            case ERROR:
            case NULL:
            case NONE:
                return false;
            case DECLARED:
                return !isProperDeclared(t);
            case WILDCARD:
                return isTricky(((WildcardType) t).getExtendsBound())
                        || isTricky(((WildcardType) t).getSuperBound());
            case TYPEVAR:
                return isTricky(((TypeVariable) t).getLowerBound())
                        || isTricky(((TypeVariable) t).getUpperBound());
            default:
                if (!k.isPrimitive()) {
                    return true;
                }
        }

        return false;
    }

    protected String getHelpText() {
        return "Must be one of:\n" +
                "\t• android.os.Parcelable, java.io.Serializable, java.io.Externalizable\n" +
                "\t• android.os.IInterface subclass (annotated with @AIDL or produced by aidl tool)\n" +
                "\t• One of types, natively supported by Parcel, or one of primitive type wrappers\n" +
                "\t• Map, List, Set and their concrete subclasses with public default constructors";
    }

    @SuppressWarnings("SimplifiableIfStatement")
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

        if (Source.getKind() == TypeKind.TYPEVAR || Target.getKind() == TypeKind.TYPEVAR) {
            if (Source.getKind() == TypeKind.TYPEVAR) {
                Source = ((TypeVariable) Source).getUpperBound();
            }

            if (Target.getKind() == TypeKind.TYPEVAR) {
                Target = ((TypeVariable) Source).getUpperBound();
            }
        }

        // arrays are covariant, so we have easy time with those
        if (Target.getKind() == TypeKind.ARRAY) {
            if (Source.getKind() != TypeKind.ARRAY) {
                // if Source is Object, we are cool, otherwise just let the failed cast happen
                return true;
            }

            // again, letting any erroneous casts happen
            if (Source.getKind().isPrimitive() || Target.getKind().isPrimitive()) {
                return true;
            }

            return castAllowed(
                    ((ArrayType) Source).getComponentType(),
                    ((ArrayType) Target).getComponentType());
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

    /**
     * Remove redundant parts from type (namely wildcards), but only if doing so does not hurt assignability
     * to the second type (assuming an assignment was possible to  to begin with).
     */
    protected TypeMirror removeRedundancy(TypeMirror subjectType, TypeMirror goalpost) {
        final TypeMirror captured = captureAll(subjectType);

        if (types.isAssignable(goalpost, captured)) {
            // wildcards weren't needed after all
            return captured;
        }

        return subjectType;
    }

    public Collection<? extends TypeMirror> getSupertypes(TypeMirror s) {
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

    private final TypeVisitor<TypeMirror, TypeMirror> BOUND_REFINER = new SharedParentRefiner();

    private final TypeVisitor<TypeMirror, TypeMirror> NESTED_BOUND_REFINER = new NestedTypeArgRefiner();

    public TypeMirror gaugeConcreteParent(TypeMirror mirror, TypeMirror returnedType) {
        final TypeKind targetKind = mirror.getKind();

        if (targetKind.isPrimitive()) {
            return mirror;
        }

        final TypeMirror foundParent = BOUND_REFINER.visit(mirror, returnedType);

        if (foundParent == null) {
            return theObject;
        }

        final TypeKind refinedKind = foundParent.getKind();

        switch (refinedKind) {
            case ARRAY:
                // component type is already refined
                return foundParent;
            case DECLARED:
                final DeclaredType asDeclared = (DeclaredType) foundParent;

                return substituteAllArgs(asDeclared);
            default:
                // WTF??
                return foundParent;
        }
    }

    private DeclaredType substituteAllArgs(DeclaredType foundParent) {
        final List<? extends TypeMirror> typeArgs = foundParent.getTypeArguments();

        if (!typeArgs.isEmpty()) {
            if (isRecursive(foundParent)) {
                return (DeclaredType) types.erasure(foundParent);
            }
        }

        final TypeMirror[] result = new TypeMirror[typeArgs.size()];

        for (int i = 0; i < typeArgs.size(); ++i) {
            final TypeMirror arg = typeArgs.get(i);

            final TypeMirror refined = NESTED_BOUND_REFINER.visit(arg, null);

            result[i] = refined == null ? types.getWildcardType(null, null) : refined;
        }

        return types.getDeclaredType((TypeElement) foundParent.asElement(), result);
    }

    public MethodSpec.Builder override(ExecutableElement method, DeclaredType enclosing, boolean primary) {
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

        if (primary) {
            for (AnnotationMirror mirror : method.getAnnotationMirrors()) {
                if (isTypeOf(Override.class, mirror.getAnnotationType())) {
                    continue;
                }

                methodBuilder.addAnnotation(AnnotationSpec.get(mirror));
            }
        }

        for (int i = 0; i < parameters.size(); i++) {
            final VariableElement param = parameters.get(i);

            final TypeName type = TypeName.get(resolvedParameterTypes.get(i));

            final Set<Modifier> modifiers = param.getModifiers();

            final ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(type, param.getSimpleName().toString())
                    .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

            if (primary) {
                for (AnnotationMirror mirror : param.getAnnotationMirrors()) {
                    parameterBuilder.addAnnotation(AnnotationSpec.get(mirror));
                }
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

    // Note: this is just rough approximation and will fail for some corner-cases (such as NullType)
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

    protected DeclaredType getBaseDeclared(TypeMirror type, DeclaredType desirableParent) {
        // if the type is raw, we have to erase the target parent
        if (isRaw(type)) {
            desirableParent = (DeclaredType) types.erasure(desirableParent);
        }

        final ArrayList<DeclaredType> parents = new ArrayList<>(1);

        final CollectionTypeRefiner refiner = new CollectionTypeRefiner(parents, desirableParent);

        refiner.visit(type);

        if (parents.isEmpty()) {
            final CharSequence nam = desirableParent.asElement().getSimpleName();

            throw new IllegalStateException("The type " + type + " appears to be subtype of " + nam + ", but the exact type parameter(s) can not be determined");
        }

        Collections.sort(parents, specificityComparator);

        return parents.get(0);
    }

    public boolean isChecked(TypeMirror throwable) {
        return !types.isSubtype(throwable, theRuntimeException) && types.isSubtype(throwable, theException);
    }

    /**
     * @return true if the argument has type args (and erasure removes them)
     */
    public boolean hasTypeArgs(TypeMirror type) {
        return !types.isSameType(types.erasure(type), type);
    }

    /**
     * @return true only if the specified type is a "raw type" (a type-erased version of parametrized type)
     */
    public boolean isRaw(TypeMirror type) {
        if (!isProperDeclared(type)) {
            return false;
        }

        final DeclaredType d = (DeclaredType) type;

        return d.getTypeArguments().isEmpty() &&
                !((TypeElement) d.asElement()).getTypeParameters().isEmpty();
    }

    protected DeclaredType lookup(Class<?> clazz) {
        return lookup(clazz.getName());
    }

    protected DeclaredType lookup(CharSequence name) {
        TypeElement typeElement = elements.getTypeElement(name);
        if (typeElement == null) {
            return null;
        }

        return types.getDeclaredType(typeElement);
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
        public TypeMirror visitDeclared(DeclaredType type, TypeMirror unused) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            final Element element = types.asElement(type);

            if (element != null) {
                final ElementKind elKind = element.getKind();

                if (elKind.isClass() || elKind.isInterface()) {
                    return type;
                }
            }

            return defaultAction(type, null);
        }

        @Override
        public TypeMirror visitWildcard(WildcardType wildcardType, TypeMirror unused) {
            TypeMirror extendsBound = wildcardType.getExtendsBound();

            if (extendsBound != null) {
                TypeMirror refined = gaugeConcreteParent(extendsBound, types.getNullType());

                if (refined != null) {
                    return types.getWildcardType(refined, null);
                }
            }

            TypeMirror superBound = wildcardType.getSuperBound();

            if (superBound != null) {
                TypeMirror refined = gaugeConcreteParent(extendsBound, types.getNullType());

                if (refined != null) {
                    return types.getWildcardType(null, refined);
                }
            }

            return null;
        }

        @Override
        public TypeMirror visitTypeVariable(TypeVariable typeVariable, TypeMirror unused) {
            TypeMirror upperBound = typeVariable.getUpperBound();
            TypeMirror lowerBound = typeVariable.getLowerBound();

            if ((upperBound == null) == (lowerBound == null)) {
                // if both bounds are null, that's by definition unbounded wildcard
                // if both aren't null —  it is not denotable, so also requires unbounded wildcard
                return null;
            }

            if (upperBound != null) {
                TypeMirror refined = BOUND_REFINER.visit(upperBound, types.getNullType());

                return types.getWildcardType(refined, null);
            } else {
                TypeMirror refined = BOUND_REFINER.visit(lowerBound, types.getNullType());

                return types.getWildcardType(null, refined);
            }
        }

        @Override
        public TypeMirror visitUnknown(TypeMirror typeMirror, TypeMirror unused) {
            return defaultAction(typeMirror, unused);
        }

        @Override
        protected TypeMirror defaultAction(TypeMirror type, TypeMirror unused) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            final List<? extends TypeMirror> superTypes = types.directSupertypes(type);

            for (TypeMirror parent : superTypes) {
                final TypeMirror discovered = visit(parent, unused);

                if (discovered != null) {
                    return discovered;
                }
            }

            return null;
        }
    }

    private final class SharedParentRefiner extends TypeKindVisitor6<TypeMirror, TypeMirror> {
        @Override
        public TypeMirror visitDeclared(DeclaredType type, TypeMirror desirableParent) {
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
        public TypeMirror visitArray(ArrayType type, TypeMirror desirableParent) {
            if (types.isAssignable(desirableParent, type)) {
                switch (desirableParent.getKind() ) {
                    case ARRAY:
                        TypeMirror selfComponent = type.getComponentType();
                        TypeMirror targetComponent = ((ArrayType) desirableParent).getComponentType();

                        final TypeMirror resultComponent = visit(selfComponent, targetComponent);

                        if (resultComponent != null) {
                            return types.getArrayType(resultComponent);
                        }
                        break;
                    case DECLARED:
                        return defaultAction(type, desirableParent);
                    default:
                        // we don't know exact rules, so try to do *something*
                        return makeDenotable(type);
                }
            }

            return null;
        }

        @Override
        public TypeMirror visitPrimitive(PrimitiveType type, TypeMirror desirableParent) {
            return types.isAssignable(desirableParent, type) ? type : null;
        }

        @Override
        public TypeMirror visitUnknown(TypeMirror t, TypeMirror typeMirror) {
            return defaultAction(t, typeMirror);
        }

        @Override
        protected TypeMirror defaultAction(TypeMirror type, TypeMirror desirableParent) {
            if (isEffectivelyObject(type)) {
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

    private final class CollectionTypeRefiner extends TypeKindVisitor6<DeclaredType, TypeMirror> {
        private final Collection<DeclaredType> input;
        private final DeclaredType parentType;

        private CollectionTypeRefiner(Collection<DeclaredType> input, DeclaredType parentType) {
            this.input = input;
            this.parentType = parentType;
        }

        @Override
        public DeclaredType visitDeclared(DeclaredType type, TypeMirror ignored) {
            if (isProperDeclared(type) && types.isSubtype(type, parentType)) {
                input.add(type);

                return null;
            } else {
                return defaultAction(type, parentType);
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

    public CodeBlock emitMapConstructorCall(TypeMirror capturedType, Object sizeLiteral) {
        final TypeMirror erased = types.erasure(capturedType);

        if (JavaVersion.atLeast(JavaVersion.JAVA_1_7)) {
            if (Util.isProperDeclared(capturedType)) {
                Collection<? extends TypeMirror> args = ((DeclaredType) capturedType).getTypeArguments();

                if (!args.isEmpty()) {
                    return literal("new $T<>($L, 1f)", erased, sizeLiteral);
                }
            }
        }

        return literal("new $T($L, 1f)", erased, sizeLiteral);
    }

    public static void swap(List<?> list, int i, int j) {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        swapInner(list, i, j);
    }

    private static <T> void swapInner(List<T> l, int i, int j) {
        l.set(i, l.set(j, l.get(i)));
    }

    public TypeMirror jokeLub(TypeMirror requested, TypeMirror returned) {
        if (types.isAssignable(requested, returned) && !isTricky(requested)) {
            return requested;
        }

        return returned;
    }

    /**
     * Prepare type for usage in array declaration by calling {@link #captureAll} on base type
     * and substituting all type parameters with wildcards.
     */
    public TypeMirror makeGenericArray(TypeMirror elementType) {
        if (elementType.getKind().isPrimitive()) {
            return elementType;
        }

        // only reified runtime type matters
        TypeMirror erased = types.erasure(elementType);

        if (isProperDeclared(erased)) {
            final DeclaredType notErasedParent = getBaseDeclared(elementType, (DeclaredType) erased);

            if (hasTypeArgs(notErasedParent)) {
                final TypeElement element = (TypeElement) notErasedParent.asElement();

                final int argCount = element.getTypeParameters().size();

                TypeMirror[] wildcards = new TypeMirror[argCount];

                Arrays.fill(wildcards, types.getWildcardType(null, null));

                return types.getDeclaredType(element, wildcards);
            }
        }

        return erased;
    }

    /**
     * Convert type to externally denotable form *without* applying erasure or capture conversion
     * (so type arguments and wildcards are preserved).
     *
     * After applying this conversion resulting type will be a proper declared type or array of proper declared elements
     * and type arguments will consist solely from proper declared types and wildcards, bound by proper declared types.
     *
     * All type variables, intersection types etc. are recursively replaced with their first denotable parent.
     *
     * This ensures better preservation of type information, compared to {@link #capture} and/or {@link #captureAll}
     * and does not have unnecessary side affects of erasure or capture conversion.
     */
    public TypeMirror makeDenotable(TypeMirror type) {
        if (type == null || type.getKind().isPrimitive()) {
            return type;
        }

        final TypeMirror cleanedUp = substituteForBaseTypes(type);

        if (cleanedUp == null) {
            return theObject;
        }

        return keepIfSameType(type, cleanedUp);
    }

    private TypeMirror substituteForBaseTypes(TypeMirror type) {
        if (type == null) {
            return null;
        }

        switch (type.getKind()) {
            case ARRAY:
                return types.getArrayType(makeDenotable(((ArrayType) type).getComponentType()));
            case WILDCARD:
                // if an unbound wildcard somehow sneaks here, just use Object
                TypeMirror extendsBound = ((WildcardType) type).getExtendsBound();
                return extendsBound == null ? theObject : makeDenotable(extendsBound);
            default:
                if (!isDenotable(type)) {
                    final TypeMirror erased = types.erasure(type);

                    final DeclaredType declaredParent = findDeclaredParent(type, (DeclaredType) erased);

                    if (declaredParent == null || !isDenotable(declaredParent)) {
                        // we are out of options
                        return theObject;
                    }

                    type = declaredParent;
                }

                return substituteAllTypeArgs((DeclaredType) type);
        }
    }

    private TypeMirror substituteAllTypeArgs(DeclaredType type) {
        final List<? extends TypeMirror> args = type.getTypeArguments();

        if (args.isEmpty()) {
            return type;
        }

        if (isRecursive(type)) {
            return types.erasure(type);
        }

        final TypeMirror[] refinedArguments = new TypeMirror[args.size()];

        for (int i = 0; i < args.size(); i++) {
            TypeMirror arg = args.get(i);

            refinedArguments[i] = substituteForTypeArg(arg);
        }

        return types.getDeclaredType((TypeElement) type.asElement(), refinedArguments);
    }

    private TypeMirror substituteForTypeArg(TypeMirror innerType) {
        if (innerType == null) {
            return null;
        }

        switch (innerType.getKind()) {
            case WILDCARD:
                TypeMirror extendsBound = ((WildcardType) innerType).getExtendsBound();
                TypeMirror superBound = ((WildcardType) innerType).getSuperBound();
                return types.getWildcardType(makeDenotable(extendsBound), makeDenotable(superBound));
            default:
                return substituteForBaseTypes(innerType);
        }
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

        return keepIfSameType(type, captureInner(type));
    }

    // Check if a is the same as b from type system's viewpoint.
    // If so, return a, else b.
    private <T extends TypeMirror> T keepIfSameType(T a, T b) {
        return types.isSameType(a, b) ? a : b;
    }

    static TypeMirror capture(Types t, TypeMirror sourceType) {
        try {
            return t.capture(sourceType);
        } catch (UnsupportedOperationException ignored) {
            return sourceType;
        }
    }

    private TypeMirror captureInner(TypeMirror type) {
        final TypeKind kind = type.getKind();

        switch (kind) {
            case ARRAY:
                return types.getArrayType(captureInner(((ArrayType) type).getComponentType()));
            default:
                final TypeMirror refined = capture(types, type);

                if (Util.isProperDeclared(refined)) {
                    return captureAllArgs((DeclaredType) refined);
                }

                final TypeMirror erasedArg = types.erasure(type);

                if (!Util.isProperDeclared(erasedArg)) {
                    return theObject;
                }

                final TypeMirror declaredParent = findDeclaredParent(type, (DeclaredType) erasedArg);

                if (declaredParent == null) {
                    return erasedArg;
                }

                final TypeMirror refinedParent = capture(types, declaredParent);

                if (Util.isProperDeclared(refinedParent)) {
                    return captureAllArgs((DeclaredType) refinedParent);
                }

                return erasedArg;
        }
    }

    private ThreadLocal<CowCloneableList<Element>> cowListCache = new ThreadLocal<>();

    /**
     * A recursive type is a type, that contains a self-references in it's type signature (and usually
     * requires a self-referencing type to be implemented). Example:
     *
     * <pre>{@code
     *
     *     public interface Recursive<XX extends Callable<XX>> {}
     *
     * }<pre/>
     *
     * Such types tend to cause stack overflow in some AIDL2 methods, so it is often safer to reject them
     * from outset push the responsibility for dealing with them onto {@link Types#erasure} and {@link Types#capture}.
     */
    public boolean isRecursive(DeclaredType suspect) {
        try (CowCloneableList<Element> list = getCached().clone()) {
            boolean result = isRecursive(suspect, list);

            list.clear();

            return result;
        }
    }

    private CowCloneableList<Element> getCached() {
        CowCloneableList<Element> list = cowListCache.get();

        if (list == null) {
            list = new CowCloneableList<>();

            cowListCache.set(list);
        }

        return list;
    }

    private boolean isRecursive(TypeMirror suspect, CowCloneableList<Element> encounteredTypeArgs) {
        Element element = types.asElement(suspect);

        // type parameters seem to be the only way to create recursive types as of Java 8
        if (element != null && element.getKind() == ElementKind.TYPE_PARAMETER) {
            if (encounteredTypeArgs.contains(element)) {
                return true;
            } else {
                encounteredTypeArgs.add(element);
            }
        } else if (suspect.getKind() == TypeKind.WILDCARD) {
            return isRecursive(((WildcardType) suspect).getExtendsBound(), encounteredTypeArgs);
        }

        if (!Util.isProperDeclared(suspect)) {
            // This branch is usually reached for type variables and intersection types
            // find all declared parents and ensure that a nasty type argument didn't sneak it's
            // way into their type signatures
            final Collection<? extends TypeMirror> supertypes = getSupertypes(suspect);

            try (CowCloneableList<Element> forDescent = encounteredTypeArgs.clone()) {
                for (TypeMirror parent : supertypes) {
                    if (isProperDeclared(parent) && !types.isSameType(suspect, parent)) {
                        int old = forDescent.size();

                        if (isRecursive(parent, forDescent)) {
                            return true;
                        }

                        if (old != forDescent.size()) forDescent.setSize(old);
                    }
                }
            }

            return false;
        }

        DeclaredType asDeclared = (DeclaredType) suspect;

        List<? extends TypeMirror> typeArgs = asDeclared.getTypeArguments();

        if (typeArgs.size() == 1) {
            return isRecursive(typeArgs.get(0), encounteredTypeArgs);
        } else {
            try (CowCloneableList<Element> forDescent = encounteredTypeArgs.clone()) {
                for (TypeMirror typeArg : typeArgs) {
                    int old = forDescent.size();

                    if (isRecursive(typeArg, forDescent)) {
                        return true;
                    }

                    if (old != forDescent.size()) forDescent.setSize(old);
                }
            }
        }

        return false;
    }

    private DeclaredType captureAllArgs(DeclaredType type) {
        final List<? extends TypeMirror> args = type.getTypeArguments();

        if (args.isEmpty()) {
            return type;
        }

        if (isRecursive(type)) {
            return (DeclaredType) types.erasure(type);
        }

        final TypeMirror[] argumentCaptures = new TypeMirror[args.size()];

        for (int i = 0; i < args.size(); i++) {
            TypeMirror arg = args.get(i);

            // clone the encountered types list to
            argumentCaptures[i] = captureInner(arg);
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
