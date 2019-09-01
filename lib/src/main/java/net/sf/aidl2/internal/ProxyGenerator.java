package net.sf.aidl2.internal;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.NameAllocator;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.InterfaceLoader;
import net.sf.aidl2.internal.codegen.TypeInvocation;
import net.sf.aidl2.internal.codegen.TypedExpression;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.exceptions.ElementException;
import net.sf.aidl2.internal.util.Util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static net.sf.aidl2.internal.util.Util.literal;

final class ProxyGenerator extends AptHelper implements AidlGenerator {
    private static final String JAVADOC = "Perform IPC calls according to the interface contract.\n" +
            "\n" +
            "You can create instances of this class, using {@link $T}.\n" +
            "\n" +
            "@deprecated — do not use this class directly in your Java code (see above)\n";

    private final ClassName iBinder = ClassName.get("android.os", "IBinder");

    private final NameAllocator baseAllocator = new NameAllocator();

    private final TypeMirror theParcel;

    public ProxyGenerator(AidlProcessor.Environment environment) {
        super(environment);

        theParcel = lookup("android.os.Parcel");
    }

    public void make(List<? super JavaFile> results, AidlModel aidlInterfaceDetails) throws ElementException, IOException {
        results.add(writeModel(aidlInterfaceDetails));
    }

    private JavaFile writeModel(AidlModel model) throws IOException, ElementException {
        final NameAllocator modelAllocator = baseAllocator.clone();

        final String name = model.clientImplName.toString();

        final TypeName interfaceType = model.interfaceName;

        final TypeElement originatingInterface = elements.getTypeElement(interfaceType.toString());

        final CharSequence pkg = elements.getPackageOf(originatingInterface).getQualifiedName();

        final DeclaredType ifType = (DeclaredType) originatingInterface.asType();

        final TypeName serverClass = ClassName.get(pkg.toString(), model.serverImplName.toString());

        final boolean useErasure = isRecursive(ifType);

        final DeclaredType ifTypeErased = (DeclaredType) types.erasure(ifType);

        final TypeSpec.Builder implClassSpec = TypeSpec.classBuilder(name);

        if (useErasure) {
            implClassSpec.addSuperinterface(TypeName.get(ifTypeErased));
        } else {
            implClassSpec.addSuperinterface(TypeName.get(ifType));
        }

        final boolean enableVersioning = getBaseEnvironment().getConfig().isVersioningEnabled();

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(iBinder, "delegate")
                .addStatement("this.$L = $L", "delegate", "delegate")
                .addException(ClassName.bestGuess("android.os.RemoteException"));

        if (enableVersioning) {
            constructor.addStatement("$T.verify($L, $T.DESCRIPTOR, $T.ipcVersionId)", AidlUtil.class, "delegate", serverClass, serverClass);
        }

        implClassSpec
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addOriginatingElement(originatingInterface)
                .addJavadoc(JAVADOC, InterfaceLoader.class)
                .addAnnotation(Deprecated.class)
                .addField(iBinder, "delegate", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(constructor.build());

        if (!useErasure) {
            final List<? extends TypeParameterElement> typeArgs = originatingInterface.getTypeParameters();

            for (TypeParameterElement superTypeArg : typeArgs) {
                implClassSpec.addTypeVariable(TypeVariableName.get(superTypeArg));
            }
        }

        for (Annotation annotation : model.migrated) {
            implClassSpec.addAnnotation(AnnotationSpec.get(annotation));
        }

        implClassSpec.addMethod(MethodSpec.overriding(asBinder.element, theIInterface, types)
                .addStatement("return delegate")
                .build());

        if (!model.methods.isEmpty()) {
            final State aidlWriter = new State(getBaseEnvironment(), modelAllocator, model.digest)
                    .allowUnchecked(isSuppressed(model.suppressed, Util.SUPPRESS_UNCHECKED))
                    .assumeFinal(model.assumeFinal);

            for (AidlMethodModel method : model.methods.values()) {
                final State methodWriter = aidlWriter.clone();

                if (isSuppressed(method.warningsSuppressedOnMethod, Util.SUPPRESS_UNCHECKED)) {
                    methodWriter.allowUnchecked(true);
                }

                final MethodSpec.Builder methodSpec = override(method.element.element,
                        useErasure ? ifTypeErased : ifType, method.isDeclaredInPrimaryFile());

                for (AidlParamModel param : method.parameters) {
                    if (param.name != null) {
                        methodWriter.allocator.newName(param.name.toString(), param);
                    } else {
                        methodWriter.allocator.newName("returnValue", param);
                    }
                }

                final String data = methodWriter.allocator.newName("data");
                final String reply = methodWriter.allocator.newName("reply");

                methodSpec.addStatement("$T $N = $T.obtain()", theParcel, data, theParcel);

                if (method.oneWay) {
                    methodSpec.addStatement("$T $N = null", theParcel, reply);
                } else {
                    methodSpec.addStatement("$T $N = $T.obtain()", theParcel, reply, theParcel);
                }

                methodSpec.beginControlFlow("try");

                if (!model.insecure) {
                    methodSpec.addStatement("$N.writeInterfaceToken($T.DESCRIPTOR)", data, serverClass);
                    methodSpec.addCode("\n");
                }

                final CodeBlock.Builder paramWriting = CodeBlock.builder();
                final CodeBlock.Builder delegateCall = CodeBlock.builder();
                final CodeBlock.Builder returnReading = CodeBlock.builder();

                final CodeBlock flags = method.oneWay
                        ? Util.literal("$T.FLAG_ONEWAY", iBinder)
                        : Util.literal("0");

                final CodeBlock code = literal("$T.TRANSACT_$L", serverClass, method.element.element.getSimpleName());

                delegateCall.addStatement("delegate.transact($L, data, reply, $L)", code, flags);

                if (!method.oneWay) {
                    delegateCall.addStatement("$N.readException()", reply);
                }

                for (AidlParamModel param : method.parameters) {
                    final State paramMarshaller = methodWriter.clone()
                            .setParameter(param);

                    if (isSuppressed(param.suppressed, Util.SUPPRESS_UNCHECKED)) {
                        paramMarshaller.allowUnchecked(true);
                    }

                    if (param.outParameter) {
                        if (param.isReturn() && param.type.getKind() != TypeKind.VOID) {
                            try {
                                readReturnValue(returnReading, paramMarshaller, reply);
                            } catch (CodegenException cde) {
                                throw new ElementException(cde, method.element.element);
                            }
                        }
                    }

                    if (param.inParameter) {
                        try {
                            writeInParam(paramWriting, paramMarshaller, data);
                        } catch (CodegenException cge) {
                            throw new ElementException(cge, Util.arg(method.element.element, param.name));
                        }
                    }
                }

                final CodeBlock writing = paramWriting.build();
                if (!writing.isEmpty()) {
                    methodSpec.addCode(writing);
                }

                methodSpec.addCode(delegateCall.build());

                final CodeBlock returned = returnReading.build();
                if (!returned.isEmpty()) {
                    methodSpec.addCode("\n");
                }

                methodSpec.addCode(returnReading.build());

                methodSpec.nextControlFlow("finally");

                methodSpec.addStatement("$N.recycle()", data);

                if (!method.oneWay) {
                    methodSpec.addStatement("$N.recycle()", reply);
                }

                methodSpec.endControlFlow();

                implClassSpec.addMethod(methodSpec.build());
            }
        }

        return JavaFile.builder(pkg.toString(), implClassSpec.build())
                .addFileComment("AUTO-GENERATED FILE.  DO NOT MODIFY.")
                .build();
    }

    private void writeInParam(CodeBlock.Builder code, State writer, String parcel) throws CodegenException {
        final CodeBlock.Builder initializationCode = CodeBlock.builder();

        writer.buildWriter(parcel).write(initializationCode, writer.type);

        final CodeBlock initializationBlock = initializationCode.build();

        if (!initializationBlock.isEmpty()) {
            code.add(initializationBlock);
            code.add("\n");
        }
    }

    private void readReturnValue(CodeBlock.Builder code, State reader, String name) throws CodegenException, IOException {
        final TypedExpression assignmentCode = reader.buildReader(name)
                .read(code, reader.type);

        code.addStatement("return $L", emitCasts(assignmentCode.type, reader.type, assignmentCode.code));
    }
}
