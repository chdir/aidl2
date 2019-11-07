package net.sf.aidl2.internal;

import android.os.IBinder;
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

import net.sf.aidl2.AidlUtil;
import net.sf.aidl2.internal.codegen.TypedExpression;
import net.sf.aidl2.internal.exceptions.CodegenException;
import net.sf.aidl2.internal.exceptions.ElementException;
import net.sf.aidl2.internal.util.Util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static net.sf.aidl2.internal.ContractHasher.TRANSACTION_ID;
import static net.sf.aidl2.internal.util.Util.literal;

final class StubGenerator extends AptHelper implements AidlGenerator {
    private static final ClassName loaderName = ClassName.bestGuess("InterfaceLoader");

    private static final ClassName binderName = ClassName.get("android.os", "IBinder");

    private static final String JAVADOC = "Handle incoming IPC calls by forwarding them to provided delegate.\n" +
            "\n" +
            "You can create instances of this class, using {@link $T}.\n" +
            "\n" +
            "@deprecated — do not use this class directly in your Java code (see above)\n";

    private final NameAllocator baseAllocator = new NameAllocator();

    private final MethodSpec onTransact;

    private final ParameterSpec code;
    private final ParameterSpec parcel;
    private final ParameterSpec returnParcel;
    private final ParameterSpec flags;

    private final FieldSpec descriptor;

    public StubGenerator(AidlProcessor.Environment environment) {
        super(environment);

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

    public void make(List<? super JavaFile> files, AidlModel model) throws ElementException, IOException {
        files.add(writeModel(model));
    }

    private JavaFile writeModel(AidlModel model) throws IOException, net.sf.aidl2.internal.exceptions.ElementException {
        final NameAllocator modelAllocator = baseAllocator.clone();

        final boolean enableVersioning = getBaseEnvironment().getConfig().isVersioningEnabled();

        final String name = model.serverImplName.toString();

        final TypeName interfaceType = model.interfaceName;

        final TypeElement originatingInterface = elements.getTypeElement(interfaceType.toString());

        final TypeSpec.Builder implClassSpec = TypeSpec.classBuilder(name)
                .superclass(ClassName.get("android.os", "Binder"))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addOriginatingElement(originatingInterface)
                .addJavadoc(JAVADOC, loaderName)
                .addAnnotation(Deprecated.class);

        String verField = null;
        if (enableVersioning) {
            verField = modelAllocator.newName("ipcVersionId");

            implClassSpec.addField(FieldSpec.builder(long.class, verField, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$LL", model.rpcVersionId)
                    .build());
        }

        TypeName stubbed = TypeName.get(typeArgsToWildcards((DeclaredType) originatingInterface.asType()));

        implClassSpec
                .addField(descriptor.toBuilder()
                        .initializer("$S", model.descriptor)
                        .build())
                .addField(stubbed, "delegate", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(stubbed, "delegate")
                        .addStatement("this.delegate = delegate")
                        .addCode("\n")
                        .addStatement("this.attachInterface(delegate, DESCRIPTOR)")
                        .build());

        for (Annotation annotation : model.migrated) {
            implClassSpec.addAnnotation(AnnotationSpec.get(annotation));
        }

        if (!model.methods.isEmpty()) {
            final State aidlReader = new State(getBaseEnvironment(), modelAllocator, model.digest)
                    .allowUnchecked((model.suppressed & Util.SUPPRESS_UNCHECKED) != 0)
                    .assumeFinal(model.assumeFinal);

            final MethodSpec.Builder onTransactSpec = onTransact.toBuilder();

            onTransactSpec.beginControlFlow("switch($N)", code);

            for (AidlMethodModel method : model.methods.values()) {
                aidlReader.allocator.newName("TRANSACT_" + method.element.element.getSimpleName(), method);
            }

            final Set<String> allSuppressed = new HashSet<>();

            boolean first = true;

            aidlReader.versionCalc().writeUTF(model.descriptor.toString());

            for (AidlMethodModel method : model.methods.values()) {
                registerConverters(implClassSpec, method.parameters, aidlReader.allocator);

                final CharSequence methodName = method.element.element.getSimpleName();

                final String transactIdField = aidlReader.allocator.get(method);

                final int transactionIdVal = method.transactionId - IBinder.FIRST_CALL_TRANSACTION;

                final CodeBlock transactionIdBlock = transactionIdVal == 0
                        ? literal("$T.FIRST_CALL_TRANSACTION", binderName)
                        : literal("$T.FIRST_CALL_TRANSACTION + $L", binderName, transactionIdVal);

                FieldSpec transactId = FieldSpec.builder(int.class, transactIdField, Modifier.FINAL, Modifier.STATIC)
                        .initializer(transactionIdBlock)
                        .build();

                implClassSpec.addField(transactId);

                if (first) {
                    first = false;

                    onTransactSpec.beginControlFlow("case $N:", transactId);
                } else {
                    onTransactSpec.nextControlFlow("case $N:", transactId);
                }

                final State methodReader = aidlReader.clone();

                methodReader.versionCalc().writeInt(TRANSACTION_ID);
                methodReader.versionCalc().writeInt(method.transactionId);
                methodReader.versionCalc().writeBoolean(model.insecure);
                methodReader.versionCalc().writeBoolean(method.oneWay);

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
                            throw new ElementException(cde, Util.arg(method.element.element, param.name));
                        }
                    }

                    if (param.outParameter) {
                        if (param.isReturn()) {
                            if (isVoid(param.type)) {
                                try {
                                    delegateCall.addStatement("delegate.$L($L)", methodName, argList(method.parameters, methodArgs));

                                    if (param.type.getKind() != TypeKind.VOID) {
                                        writeOutParameter(paramsWrite, paramMarshaller, returnParcel.name);
                                    }
                                } catch (CodegenException cde) {
                                    throw new ElementException(cde, method.element.element);
                                }
                            } else {
                                try {
                                    TypeMirror requiredReturn = writeReturnValue(paramsWrite, paramMarshaller, returnParcel.name);

                                    final CodeBlock callCode = literal("delegate.$L($L)", methodName, argList(method.parameters, methodArgs));

                                    delegateCall.addStatement("final $T returnValue = $L",
                                            requiredReturn, emitCasts(param.type, requiredReturn, callCode));
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
                    onTransactSpec.addCode("\n");

                    onTransactSpec.addCode(paramsWrite.build());
                }

                Collections.addAll(allSuppressed, method.suppressed);

                onTransactSpec.addStatement("return true");
            }

            if (enableVersioning && !model.methods.containsKey(AidlUtil.VERSION_TRANSACTION)) {
                onTransactSpec.nextControlFlow("case $T.$N:", AidlUtil.class, "VERSION_TRANSACTION");
                onTransactSpec.addStatement("$L.enforceInterface(this.getInterfaceDescriptor())", parcel.name);
                onTransactSpec.addCode("\n");
                onTransactSpec.addStatement("$N.writeNoException()", returnParcel.name);
                onTransactSpec.addStatement("$N.writeLong($N)", returnParcel.name, verField);
                onTransactSpec.addCode("\n");
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

        return JavaFile.builder(pkg.toString(), implClassSpec.build())
                .addFileComment("AUTO-GENERATED FILE.  DO NOT MODIFY.")
                .build();
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

        return literal(String.join(", ", Collections.nCopies(params.size(), "$L")), result.toArray());
    }

    private TypedExpression readInParameter(CodeBlock.Builder code, State reader, String name) throws CodegenException, IOException {
        final CodeBlock.Builder initializationCode = CodeBlock.builder();

        final TypedExpression assignmentCode = reader.buildReader(name)
                .read(initializationCode, reader.type);

        final CodeBlock initializationBlock = initializationCode.build();

        if (initializationBlock.isEmpty()) {
            TypeMirror resultType = gaugeConcreteParent(reader.type, assignmentCode.type);

            code.addStatement("final $T $N = $L", resultType, reader.name,
                    emitCasts(assignmentCode.type, resultType, assignmentCode.code));
            code.add("\n");

            return new TypedExpression(literal(reader.name.toString()), resultType);
        } else {
            code.add("$L", initializationBlock);
            code.add("\n");

            return assignmentCode;
        }
    }

    private void writeOutParameter(CodeBlock.Builder code, State writer, String name) throws CodegenException {
        writer.buildWriter(name)
                .write(code, writer.type);

        code.add("\n");
    }

    private TypeMirror writeReturnValue(CodeBlock.Builder code, State writer, String name) throws CodegenException {
        TypeMirror actualReturn = writer.buildWriter(name)
                .writeReturnValue(code, writer.type);

        code.add("\n");

        return actualReturn;
    }
}