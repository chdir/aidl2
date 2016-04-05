package net.sf.aidl2.internal;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.internal.codegen.MethodInstantiation;
import net.sf.aidl2.internal.util.Util;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.TypeKindVisitor6;
import javax.lang.model.util.Types;

import static net.sf.aidl2.internal.util.Util.getQualifiedName;
import static net.sf.aidl2.internal.util.Util.isTypeOf;
import static net.sf.aidl2.internal.util.Util.literal;

public abstract class AptHelper implements ProcessingEnvironment {
    private final net.sf.aidl2.internal.AidlProcessor.Environment environment;

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

    protected final MethodInstantiation onTransact;
    protected final MethodInstantiation asBinder;

    public AptHelper(net.sf.aidl2.internal.AidlProcessor.Environment environment) {
        this.environment = environment;

        this.elements = environment.getElementUtils();
        this.types = environment.getTypeUtils();

        theObject = lookup(Object.class);
        voidClass = lookup(Void.class);
        theThrowable = lookup(Throwable.class);
        theException = lookup(Exception.class);
        theRuntimeException = lookup(RuntimeException.class);

        theBinder = lookup("android.os.Binder");
        theIInterface = lookup("android.os.IInterface");
        theCreator = lookup("android.os.Parcelable.Creator");

        onTransact = lookupMethod(theBinder, "onTransact", int.class, "Parcel", "Parcel", int.class);
        asBinder = lookupMethod(theIInterface, "asBinder");
    }

    public boolean isSubsignature(MethodInstantiation m1, MethodInstantiation m2) {
        return m1.element.getSimpleName().equals(m2.element.getSimpleName())
                && types.isSubsignature(m1.type, m2.type);
    }

    public CodeBlock emitCasts(TypeMirror t, TypeMirror t2, CodeBlock input) {
        if (types.isAssignable(t, t2)) {
            // no need for casting
            return input;
        } else {
            final TypeMirror captured = captureAll(t2);

            if (types.isAssignable(captured, t2)) {
                // emit a simple unchecked cast
                return literal("($T) $L", captured, input);
            } else {
                // emit cast via the helper method
                // TODO: warn user
                return literal("$T.unsafeCast($L)", ClassName.get(AidlUtil.class), input);
            }
        }
    }

    public CodeBlock forceCasts(TypeMirror t, TypeMirror t2, CodeBlock input) {
        final TypeKind t2Kind = t2.getKind();

        // avoid redundant casts at least when no bounds are involved
        switch (t2Kind) {
            case DECLARED:
            case ARRAY:
                if (t.getKind() == t2.getKind() && isRaw(t) && isRaw(t2)) {
                    return emitCasts(t, t2, input);
                }
            default:
                if (t2Kind.isPrimitive()) {
                    return emitCasts(t, t2, input);
                }
        }

        final TypeMirror captured = captureAll(t2);

        if (types.isAssignable(captured, t2)) {
            // emit a simple unchecked cast
            return literal("($T) $L", captured, input);
        } else {
            // emit cast via the helper method
            // TODO: warn user
            return literal("$T.unsafeCast($L)", ClassName.get(AidlUtil.class), input);
        }
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

    public boolean isEffectivelyObject(TypeMirror type) {
        return types.isSameType(types.erasure(type), theObject);
    }

    public MethodInstantiation lookupMethod(DeclaredType type, CharSequence methodName, Object... argv) {
        methodSearch:
        for (ExecutableElement method : ElementFilter.methodsIn(type.asElement().getEnclosedElements())) {
            final Collection<? extends Modifier> modifiers = method.getModifiers();

            if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.STATIC)) {
                continue;
            }

            if (!method.getSimpleName().contentEquals(methodName)) {
                continue;
            }

            if (argv.length == 0) {
                if (!method.getParameters().isEmpty()) {
                    continue;
                }
            } else {
                final ExecutableType methodType = (ExecutableType) types.asMemberOf(type, method);

                final List<? extends TypeMirror> args = methodType.getParameterTypes();

                if (args.size() != argv.length) {
                    continue;
                }

                for (int i = 0; i < argv.length; i++) {
                    if (argv[i] instanceof Class<?>) {
                        if (!Util.isTypeOf((Class) argv[i], args.get(i))) {
                            continue methodSearch;
                        }
                    } else {
                        final TypeElement named = (TypeElement) ((DeclaredType) args.get(i)).asElement();

                        if (!argv[i].toString().contentEquals(named.getSimpleName())) {
                            continue methodSearch;
                        }
                    }
                }
            }

            final ExecutableType methodType = (ExecutableType) types.asMemberOf(type, method);

            return new MethodInstantiation(method, methodType);
        }

        throw new IllegalArgumentException("Failed to find instance method \"" + methodName + "\" in " + type);
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

    private class DeclaredTypeRefiner extends TypeKindVisitor6<DeclaredType, DeclaredType> {
        @Override
        public DeclaredType visitDeclared(DeclaredType type, DeclaredType desirableParent) {
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
        protected DeclaredType defaultAction(TypeMirror type, DeclaredType desirableParent) {
            if (types.isSameType(type, theObject)) {
                return null;
            }

            final List<? extends TypeMirror> superTypes = types.directSupertypes(type);

            for (TypeMirror parent : superTypes) {
                if (types.isSameType(types.erasure(type), types.erasure(parent))) {
                    continue;
                }

                final DeclaredType discovered = visit(parent, desirableParent);

                if (discovered != null) {
                    return discovered;
                }
            }

            return null;
        }
    }

    private final class ConcreteTypeRefiner extends DeclaredTypeRefiner {
        @Override
        public DeclaredType visitDeclared(DeclaredType type, DeclaredType desirableParent) {
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

    private final TypeVisitor<DeclaredType, DeclaredType> CONCRETE_REFINER = new ConcreteTypeRefiner();

    private final TypeVisitor<DeclaredType, DeclaredType> DECLARED_REFINER = new DeclaredTypeRefiner();

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

    public CodeBlock emitDefaultConstructorCall(TypeMirror capturedType) {
        final TypeMirror erased = types.erasure(capturedType);

        if (net.sf.aidl2.internal.util.JavaVersion.atLeast(net.sf.aidl2.internal.util.JavaVersion.JAVA_1_7)) {
            if (Util.isProperClass(capturedType)) {
                Collection<? extends TypeMirror> args = ((DeclaredType) capturedType).getTypeArguments();

                if (!args.isEmpty()) {
                    return literal("new $T<>()", erased);
                }
            }
        }

        return literal("new $T()", erased);
    }

    /**
     * Transform a type by recursively applying capture conversion. If created type can not be
     * assigned to original type, return an erasure instead.
     *
     * @param type concrete (non-abstract) declared type
     * @return transformation of {@code type}
     */
    public TypeMirror captureAll(TypeMirror type) {
        if (type == null) return null;

        if (type.getKind().isPrimitive()) {
            return type;
        }

        final DeclaredType horror = captureInner(type, new ArrayList<>());

        if (types.isAssignable(horror, type)) {
            return horror;
        } else {
            return types.erasure(type);
        }
    }

    private DeclaredType captureInner(TypeMirror type, List<TypeMirror> encountered) {
        final TypeKind kind = type.getKind();

        switch (kind) {
            case DECLARED:
                DeclaredType prepared;

                TypeMirror refined = types.capture(type);

                if (Util.isProperClass(refined)) {
                    prepared = captureAllArgs((DeclaredType) refined, encountered);
                } else {
                    refined = types.erasure(refined);

                    if (Util.isProperClass(refined)) {
                        prepared = (DeclaredType) refined;
                    } else {
                        prepared = theObject;
                    }
                }

                return prepared;
            default:
                TypeMirror erasedArg = types.erasure(type);

                if (Util.isProperClass(erasedArg)) {
                    return (DeclaredType) erasedArg;
                } else {
                    return theObject;
                }
        }
    }

    private DeclaredType captureAllArgs(DeclaredType type, List<TypeMirror> encountered) {
        final TypeMirror erased = types.erasure(type);

        for (TypeMirror encounteredType : encountered) {
            if (types.isSameType(erased, encounteredType)) {
                return Util.isProperClass(erased) ? (DeclaredType) erased : theObject;
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

    public net.sf.aidl2.internal.AidlProcessor.Environment getBaseEnvironment() {
        return environment;
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
