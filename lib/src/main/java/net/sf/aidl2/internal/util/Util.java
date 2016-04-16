package net.sf.aidl2.internal.util;

import com.squareup.javapoet.CodeBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;

public class Util {
    public static final int SUPPRESS_UNCHECKED = 0b00000000000000000000000000000001;
    public static final int SUPPRESS_AIDL = 0b00000000000000000000000000000011;
    public static final int SUPPRESS_ALL = 0b11111111111111111111111111111111;

    public static int getSuppressed(@NotNull Element element) {
        return getSuppressed(element.getAnnotation(SuppressWarnings.class));
    }

    public static int getSuppressed(@Nullable SuppressWarnings annotation) {
        if (annotation == null) {
            return 0;
        }

        final String[] values = annotation.value();

        for (String value : values) {
            if ("all".equals(value)) {
                return SUPPRESS_ALL;
            } else if ("aidl2".equals(value)) {
                return SUPPRESS_AIDL;
            } else if ("unchecked".equals(value)) {
                return SUPPRESS_UNCHECKED;
            }
        }

        return 0;
    }

    public static boolean isNullable(Element element, boolean defaultNullable) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            String annotationName = mirror.getAnnotationType().toString();

            final int lastDot = annotationName.lastIndexOf('.');
            if (lastDot != -1) {
                annotationName = annotationName.substring(lastDot, annotationName.length());
            }

            switch (annotationName) {
                case "Nullable":
                    return true;
                case "NotNull":
                case "NonNull":
                    return false;
            }
        }

        return defaultNullable;
    }

    public static boolean isNullable(TypeMirror annotated, boolean defaultNullable) {
        if (!JavaVersion.atLeast(JavaVersion.JAVA_1_8)) {
            return defaultNullable;
        }

        for (AnnotationMirror mirror : annotated.getAnnotationMirrors()) {
            String annotationName = mirror.getAnnotationType().toString();

            final int lastDot = annotationName.lastIndexOf('.');
            if (lastDot != -1) {
                annotationName = annotationName.substring(lastDot, annotationName.length());
            }

            switch (annotationName) {
                case "Nullable":
                    return true;
                case "NotNull":
                case "NonNull":
                    return false;
            }
        }

        return defaultNullable;
    }

    public static @Nullable AnnotationMirror getAnnotation(Iterable<? extends AnnotationMirror> elementMirrors, Class<? extends Annotation> clazz) {
        for (AnnotationMirror mirror : elementMirrors) {
            if (isTypeOf(clazz, mirror.getAnnotationType())) {
                return mirror;
            }
        }

        return null;
    }

    public static @Nullable AnnotationMirror getAnnotation(Element element, Class<? extends Annotation> clazz) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (isTypeOf(clazz, mirror.getAnnotationType())) {
                return mirror;
            }
        }

        return null;
    }

    public static String getTypeName(Class<?> clazz) {
        if (JavaVersion.atLeast(JavaVersion.JAVA_1_8)) {
            return clazz.getTypeName();
        } else {
            return clazz.getName();
        }
    }

    public static boolean isTypeOf(final Class<?> clazz, TypeMirror type) {
        return type.accept(new SimpleTypeVisitor6<Boolean, Void>() {
            @Override protected Boolean defaultAction(TypeMirror type, Void ignored) {
                return getTypeName(clazz).equals(type.toString());
            }

            @Override public Boolean visitNoType(NoType noType, Void p) {
                return noType.getKind().equals(TypeKind.VOID) && clazz.equals(Void.TYPE);
            }

            @Override public Boolean visitPrimitive(PrimitiveType type, Void p) {
                switch (type.getKind()) {
                    case BOOLEAN:
                        return clazz.equals(Boolean.TYPE);
                    case BYTE:
                        return clazz.equals(Byte.TYPE);
                    case CHAR:
                        return clazz.equals(Character.TYPE);
                    case DOUBLE:
                        return clazz.equals(Double.TYPE);
                    case FLOAT:
                        return clazz.equals(Float.TYPE);
                    case INT:
                        return clazz.equals(Integer.TYPE);
                    case LONG:
                        return clazz.equals(Long.TYPE);
                    case SHORT:
                        return clazz.equals(Short.TYPE);
                    default:
                        return false;
                }
            }

            @Override public Boolean visitArray(ArrayType array, Void p) {
                return clazz.isArray() && isTypeOf(clazz.getComponentType(), array.getComponentType());
            }

            @Override public Boolean visitDeclared(DeclaredType type, Void ignored) {
                final Name name = getQualifiedName(type);

                return  name != null && name.contentEquals(clazz.getCanonicalName());
            }
        }, null);
    }

    public static boolean isProperClass(TypeMirror declared) {
        return declared != null
                && declared.getKind() == TypeKind.DECLARED
                && getQualifiedName((DeclaredType) declared) != null;
    }

    private static final ElementVisitor<Name, Void> ELEMENT_NAME_REFINER = new SimpleElementVisitor6<Name, Void>() {
        @Override
        public Name visitType(TypeElement typeElement, Void unused) {
            return typeElement.getQualifiedName();
        }

        @Override
        protected Name defaultAction(Element element, Void aVoid) {
            return null;
        }
    };

    private static final ElementVisitor<Name, Void> ELEMENT_SIMPLE_NAME_REFINER = new SimpleElementVisitor6<Name, Void>() {
        @Override
        public Name visitType(TypeElement typeElement, Void unused) {
            return typeElement.getSimpleName();
        }

        @Override
        protected Name defaultAction(Element element, Void aVoid) {
            return null;
        }
    };

    public static @Nullable Name getQualifiedName(DeclaredType declared) {
        Element element = declared.asElement();

        if (element == null) {
            return null;
        }

        return ELEMENT_NAME_REFINER.visit(element);
    }

    public static @Nullable Name getSimpleName(DeclaredType declared) {
        Element element = declared.asElement();

        if (element == null) {
            return null;
        }

        return ELEMENT_SIMPLE_NAME_REFINER.visit(element);
    }

    public static VariableElement arg(ExecutableElement method, CharSequence name) {
        final String nameStr = name.toString();

        for (VariableElement arg : method.getParameters()) {
            if (nameStr.contentEquals(arg.getSimpleName())) {
                return arg;
            }
        }

        throw new IllegalArgumentException("Unable to find argument by name: " + name);
    }

    public static CodeBlock literal(CharSequence format, Object... args) {
        return CodeBlock.builder().add(format.toString(), args).build();
    }

    public static boolean isAIDL2method(ExecutableElement method) {
        final Set<Modifier> modifiers = method.getModifiers();

        return !modifiers.contains(Modifier.STATIC)
                && modifiers.contains(Modifier.ABSTRACT)
                && modifiers.contains(Modifier.PUBLIC);
    }

    public static boolean matches(Object type, TypeMirror mirror) {
        if (type instanceof Class<?>) {
            if (Util.isTypeOf((Class) type, mirror)) {
                return true;
            }
        } else {
            if (mirror.getKind() == TypeKind.DECLARED) {
                final Name typeName = getSimpleName((DeclaredType) mirror);

                if (typeName != null) {
                    if (type.toString().contentEquals(typeName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
