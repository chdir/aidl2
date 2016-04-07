package net.sf.aidl2.internal;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.NameAllocator;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.sf.aidl2.internal.codegen.TypedExpression;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.exceptions.ElementException;
import net.sf.aidl2.internal.util.Util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class StubGenerator extends AptHelper implements AidlGenerator {
    private static final ClassName loaderName = ClassName.bestGuess("InterfaceLoader");

    private static final String JAVADOC = "Handle incoming IPC calls by forwarding them to provided delegate.\n" +
            "\n" +
            "You can create instances of this class, using {@link $T}.\n" +
            "\n" +
            "@deprecated — do not use this class directly in your Java code (see above)\n";

    private final NameAllocator baseAllocator = new NameAllocator();

    private final List<AidlModel> models;

    private final MethodSpec onTransact;

    private final ParameterSpec code;
    private final ParameterSpec parcel;
    private final ParameterSpec returnParcel;
    private final ParameterSpec flags;

    private final FieldSpec descriptor;

    public StubGenerator(AidlProcessor.Environment environment, List<AidlModel> models) {
        super(environment);

        this.models = models;

        onTransact = MethodSpec.overriding(super.onTransact.element).build();

        code = onTransact.parameters.get(0);
        parcel = onTransact.parameters.get(1);
        returnParcel = onTransact.parameters.get(2);
        flags = onTransact.parameters.get(3);

        descriptor = FieldSpec.builder(String.class, "DESCRIPTOR", Modifier.FINAL, Modifier.STATIC)
                .build();

        baseAllocator.newName(code.name);
        baseAllocator.newName(parcel.name);
        baseAllocator.newName(returnParcel.name);
        baseAllocator.newName(flags.name);

        baseAllocator.newName(descriptor.name);

        baseAllocator.newName("returnValue");
    }

    public void make(Filer filer) throws net.sf.aidl2.internal.exceptions.ElementException, IOException {
        for (AidlModel model : models) {
            writeModel(model, filer);
        }
    }

    private void writeModel(AidlModel model, Filer filer) throws IOException, net.sf.aidl2.internal.exceptions.ElementException {
        final String name = model.serverImplName.toString();

        final TypeName interfaceType = model.interfaceName;

        final TypeElement originatingInterface = elements.getTypeElement(interfaceType.toString());

        final TypeSpec.Builder implClassSpec = TypeSpec.classBuilder(name)
                .superclass(ClassName.bestGuess("android.os.Binder"))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addOriginatingElement(originatingInterface)
                .addJavadoc(JAVADOC, loaderName)
                .addAnnotation(Deprecated.class)
                .addField(descriptor.toBuilder()
                        .initializer("$S", model.descriptor)
                        .build())
                .addField(interfaceType, "delegate", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(interfaceType, "delegate")
                        .addStatement("this.delegate = delegate")
                        .addCode("\n")
                        .addStatement("this.attachInterface(delegate, DESCRIPTOR)")
                        .build());

        for (Annotation annotation : model.migrated) {
            implClassSpec.addAnnotation(AnnotationSpec.get(annotation));
        }

        if (!model.methods.isEmpty()) {
            final State aidlReader = new State(getBaseEnvironment(), baseAllocator)
                    .allowUnchecked((model.suppressed & Util.SUPPRESS_UNCHECKED) != 0);

            final MethodSpec.Builder onTransactSpec = onTransact.toBuilder();

            onTransactSpec.beginControlFlow("switch($N)", code);

            for (AidlMethodModel method : model.methods) {
                aidlReader.allocator.newName("TRANSACT_" + method.element.element.getSimpleName(), method);
            }

            final Set<String> allSuppressed = new HashSet<>();

            for (int i = 0; i < model.methods.size(); i++) {
                final AidlMethodModel method = model.methods.get(i);

                final CharSequence methodName = method.element.element.getSimpleName();

                final String transactIdField = aidlReader.allocator.get(method);

                FieldSpec transactId = FieldSpec.builder(int.class, transactIdField, Modifier.FINAL, Modifier.STATIC)
                        .initializer("$T.FIRST_CALL_TRANSACTION + $L", ClassName.bestGuess("android.os.IBinder"), i)
                        .build();

                implClassSpec.addField(transactId);

                if (i == 0) {
                    onTransactSpec.beginControlFlow("case $N:", transactId);
                } else {
                    onTransactSpec.nextControlFlow("case $N:", transactId);
                }

                final State methodReader = aidlReader.clone();

                if ((method.warningsSuppressedOnMethod & Util.SUPPRESS_UNCHECKED) != 0) {
                    methodReader.allowUnchecked(true);
                }

                if (!model.insecure) {
                    onTransactSpec.addStatement("$L.enforceInterface(this.getInterfaceDescriptor())", parcel.name);
                    onTransactSpec.addCode("\n");
                }

                for (AidlParamModel param : method.parameters) {
                    if (param.name != null) {
                        methodReader.allocator.newName(param.name.toString(), param);
                    }
                }

                final CodeBlock.Builder paramsRead = CodeBlock.builder();
                final CodeBlock.Builder delegateCall = CodeBlock.builder();
                final CodeBlock.Builder paramsWrite = CodeBlock.builder();

                final List<TypedExpression> methodArgs = new ArrayList<>();

                for (AidlParamModel param : method.parameters) {
                    final State paramMarshaller = methodReader.clone()
                            .setParameter(param);

                    if ((param.suppressed & Util.SUPPRESS_UNCHECKED) != 0) {
                        paramMarshaller.allowUnchecked(true);
                    }

                    if (param.inParameter) {
                        try {
                            methodArgs.add(readInParameter(paramsRead, paramMarshaller, parcel.name));
                        } catch (CodegenException cde) {
                            throw new ElementException(cde, net.sf.aidl2.internal.util.Util.arg(method.element.element, param.name));
                        }
                    }

                    if (param.outParameter) {
                        if (param.isReturn()) {
                            if (isVoid(param.type)) {
                                try {
                                    delegateCall.addStatement("this.delegate.$L($L)", methodName, argList(method.parameters, methodArgs));

                                    if (param.type.getKind() != TypeKind.VOID) {
                                        writeOutParameter(paramsWrite, paramMarshaller, returnParcel.name);
                                    }
                                } catch (CodegenException cde) {
                                    throw new ElementException(cde, method.element.element);
                                }
                            } else {
                                try {
                                    TypeMirror requiredReturn = writeOutParameter(paramsWrite, paramMarshaller, returnParcel.name);

                                    final CodeBlock callCode = Util.literal("this.delegate.$L($L)", methodName, argList(method.parameters, methodArgs));

                                    delegateCall.addStatement("$T returnValue = $L",
                                            requiredReturn, forceCasts(param.type, requiredReturn, callCode));
                                } catch (CodegenException cde) {
                                    throw new ElementException(cde, method.element.element);
                                }
                            }
                        }
                    }
                }

                onTransactSpec.addCode(paramsRead.build());
                onTransactSpec.addCode(delegateCall.build());

                if (!method.oneWay) {
                    onTransactSpec.addStatement("$N.writeNoException()", returnParcel);

                    onTransactSpec.addCode(paramsWrite.build());
                }

                Collections.addAll(allSuppressed, method.suppressed);

                onTransactSpec.addStatement("return true");
            }

            // close last switch case
            onTransactSpec.endControlFlow();

            // close entire switch
            onTransactSpec.endControlFlow();

            onTransactSpec.addStatement("return super.onTransact($N, $N, $N, $N)", code, parcel, returnParcel, flags);

            if (!allSuppressed.isEmpty()) {
                final AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class);

                for (String suppressed : allSuppressed) {
                    builder.addMember("value", "$S", suppressed);
                }

                onTransactSpec.addAnnotation(builder.build());
            }

            implClassSpec.addMethod(onTransactSpec.build());
        }

        final CharSequence pkg = elements.getPackageOf(originatingInterface).getQualifiedName();

        JavaFile.builder(pkg.toString(), implClassSpec.build())
                .addFileComment("AUTO-GENERATED FILE.  DO NOT MODIFY.")
                .build()
                .writeTo(filer);
    }

    private CodeBlock argList(AidlParamModel[] parameters, List<TypedExpression> params) {
        if (params.isEmpty()) {
            return CodeBlock.builder().build();
        }

        final List<CodeBlock> result = new ArrayList<>(parameters.length);

        for (int i = 0; i < params.size(); i++) {
            final TypedExpression param = params.get(i);

            result.add(emitCasts(param.type, parameters[i].type, param.code));
        }

        return Util.literal(String.join(", ", Collections.nCopies(params.size(), "$L")), result.toArray());
    }

    private TypedExpression readInParameter(CodeBlock.Builder code, State reader, String name) throws CodegenException {
        final CodeBlock.Builder initializationCode = CodeBlock.builder();

        final TypedExpression assignmentCode = reader.buildReader(name)
                .read(initializationCode, reader.type);

        final CodeBlock initializationBlock = initializationCode.build();

        if (initializationBlock.isEmpty()) {
            TypeMirror resultType = gaugeConcreteParent(reader.type, assignmentCode.type);

            code.addStatement("final $T $N = $L", resultType, reader.name,
                    emitCasts(assignmentCode.type, resultType, assignmentCode.code));
            code.add("\n");

            return new TypedExpression(Util.literal(reader.name.toString()), resultType);
        } else {
            code.add("$L", initializationBlock);
            code.add("\n");

            return assignmentCode;
        }
    }

    private TypeMirror writeOutParameter(CodeBlock.Builder code, State writer, String name) throws CodegenException {
        TypeMirror requiredReturn = writer.buildWriter(name)
                .write(code, writer.type);

        code.add("\n");

        return requiredReturn;
    }
}