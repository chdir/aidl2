package net.sf.aidl2.internal.util;

import net.sf.aidl2.internal.AidlProcessor;
import net.sf.aidl2.internal.AptHelper;

import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;

/**
 * A utility class that traverses {@link Element} instances and ensures that all type information
 * is present and resolvable.
 */
public final class SuperficialValidation extends AptHelper {
    public SuperficialValidation(AidlProcessor.Environment environment) {
        super(environment);
    }

    public Element validateElements(Iterable<? extends Element> elements) {
        for (Element element : elements) {
            final Element invalidPart = validateElement(element);

            if (invalidPart != null) {
                return invalidPart;
            }
        }
        return null;
    }

    private final ElementVisitor<Element, Void> ELEMENT_VALIDATING_VISITOR =
            new SimpleElementVisitor6<Element, Void>() {
                @Override
                public Element visitPackage(PackageElement e, Void p) {
                    // don't validate enclosed elements because it will return types in the package
                    return validateAnnotations(e.getAnnotationMirrors()) ? null : e;
                }

                @Override
                public Element visitType(TypeElement e, Void p) {
                    if (!isValidBaseElement(e) || !validateTypes(e.getInterfaces())) {
                        return e;
                    }

                    final TypeMirror superclass = e.getSuperclass();

                    if (superclass.getKind() != TypeKind.NONE && !validateType(superclass)) {
                        return e;
                    }

                    final Element firstInvalidTypeArg = validateElements(e.getTypeParameters());

                    if (firstInvalidTypeArg != null) {
                        return firstInvalidTypeArg;
                    }

                    return null;
                }

                @Override
                public Element visitVariable(VariableElement e, Void p) {
                    return isValidBaseElement(e) ? null : e;
                }

                @Override
                public Element visitExecutable(ExecutableElement e, Void p) {
                    final AnnotationValue defaultValue = e.getDefaultValue();

                    if (defaultValue != null && validateAnnotationValue(defaultValue, e.getReturnType())
                            || !isValidBaseElement(e)
                            || !validateType(e.getReturnType())
                            || !validateTypes(e.getThrownTypes())) {
                        return e;
                    }

                    final Element firstInvalidTypeArg = validateElements(e.getTypeParameters());

                    if (firstInvalidTypeArg != null) {
                        return firstInvalidTypeArg;
                    }

                    final Element firstInvalidParam = validateElements(e.getParameters());

                    if (firstInvalidParam != null) {
                        return firstInvalidParam;
                    }

                    return null;
                }

                @Override
                public Element visitTypeParameter(TypeParameterElement e, Void p) {
                    return isValidBaseElement(e) && validateTypes(e.getBounds()) ? null : e;
                }

                @Override
                public Element visitUnknown(Element e, Void p) {
                    // just assume that unknown elements are OK
                    return null;
                }
            };

    public Element validateElement(Element element) {
        return element.accept(ELEMENT_VALIDATING_VISITOR, null);
    }

    private boolean isValidBaseElement(Element e) {
        return validateType(e.asType())
                && validateAnnotations(e.getAnnotationMirrors())
                && validateElements(e.getEnclosedElements()) == null;
    }

    private boolean validateTypes(Iterable<? extends TypeMirror> types) {
        for (TypeMirror type : types) {
            if (!validateType(type)) {
                return false;
            }
        }
        return true;
    }

    /*
     * This visitor does not test type variables specifically, but it seems that that is not actually
     * an issue.  Javac turns the whole type parameter into an error type if it can't figure out the
     * bounds.
     */
    private final TypeVisitor<Boolean, Void> TYPE_VALIDATING_VISITOR =
            new SimpleTypeVisitor6<Boolean, Void>() {
                @Override
                protected Boolean defaultAction(TypeMirror t, Void p) {
                    return true;
                }

                @Override
                public Boolean visitArray(ArrayType t, Void p) {
                    return validateType(t.getComponentType());
                }

                @Override
                public Boolean visitDeclared(DeclaredType t, Void p) {
                    return validateTypes(t.getTypeArguments());
                }

                @Override
                public Boolean visitError(ErrorType t, Void p) {
                    return false;
                }

                @Override
                public Boolean visitUnknown(TypeMirror t, Void p) {
                    // just make the default choice for unknown types
                    return defaultAction(t, p);
                }

                @Override
                public Boolean visitWildcard(WildcardType t, Void p) {
                    TypeMirror extendsBound = t.getExtendsBound();
                    TypeMirror superBound = t.getSuperBound();
                    return (extendsBound == null || validateType(extendsBound))
                            && (superBound == null || validateType(superBound));
                }

                @Override
                public Boolean visitExecutable(ExecutableType t, Void p) {
                    return validateTypes(t.getParameterTypes())
                            && validateType(t.getReturnType())
                            && validateTypes(t.getThrownTypes())
                            && validateTypes(t.getTypeVariables());
                }
            };

    private boolean validateType(TypeMirror type) {
        return type.accept(TYPE_VALIDATING_VISITOR, null);
    }

    private boolean validateAnnotations(Iterable<? extends AnnotationMirror> annotationMirrors) {
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            if (!validateAnnotation(annotationMirror)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateAnnotation(AnnotationMirror annotationMirror) {
        return validateType(annotationMirror.getAnnotationType())
                && validateAnnotationValues(annotationMirror.getElementValues());
    }

    @SuppressWarnings("unused")
    private boolean validateAnnotationValues(
            Map<? extends ExecutableElement, ? extends AnnotationValue> valueMap) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> valueEntry :
                valueMap.entrySet()) {
            TypeMirror expectedType = valueEntry.getKey().getReturnType();
            if (!validateAnnotationValue(valueEntry.getValue(), expectedType)) {
                return false;
            }
        }
        return true;
    }

    private final AnnotationValueVisitor<Boolean, TypeMirror> VALUE_VALIDATING_VISITOR =
            new SimpleAnnotationValueVisitor6<Boolean, TypeMirror>() {
                @Override
                protected Boolean defaultAction(Object o, TypeMirror expectedType) {
                    return Util.isTypeOf(o.getClass(), expectedType) && (!(o instanceof String) || !"<error>".equals(o));
                }

                @Override
                public Boolean visitUnknown(AnnotationValue av, TypeMirror expectedType) {
                    // just take the default action for the unknown
                    return defaultAction(av, expectedType);
                }

                @Override
                public Boolean visitAnnotation(AnnotationMirror a, TypeMirror expectedType) {
                    return true;
                }

                @Override
                public Boolean visitArray(List<? extends AnnotationValue> values, TypeMirror expectedType) {
                    return expectedType.getKind().equals(TypeKind.ARRAY);
                }

                @Override
                public Boolean visitEnumConstant(VariableElement enumConstant, TypeMirror expectedType) {
                    return types.isSubtype(enumConstant.asType(), expectedType);
                }

                @Override
                public Boolean visitType(TypeMirror type, TypeMirror ignored) {
                    // We could check assignability here, but would require a Types instance. Since this
                    // isn't really the sort of thing that shows up in a bad AST from upstream compilation
                    // we ignore the expected type and just validate the type.  It might be wrong, but
                    // it's valid.
                    return validateType(type);
                }

                @Override
                public Boolean visitBoolean(boolean b, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.BOOLEAN;
                }

                @Override
                public Boolean visitByte(byte b, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.BYTE;
                }

                @Override
                public Boolean visitChar(char c, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.CHAR;
                }

                @Override
                public Boolean visitDouble(double d, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.DOUBLE;
                }

                @Override
                public Boolean visitFloat(float f, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.FLOAT;
                }

                @Override
                public Boolean visitInt(int i, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.INT;
                }

                @Override
                public Boolean visitLong(long l, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.LONG;
                }

                @Override
                public Boolean visitShort(short s, TypeMirror expectedType) {
                    return expectedType.getKind() == TypeKind.SHORT;
                }
            };

    private boolean validateAnnotationValue(
            AnnotationValue annotationValue, TypeMirror expectedType) {
        return VALUE_VALIDATING_VISITOR.visit(annotationValue, expectedType);
    }
}