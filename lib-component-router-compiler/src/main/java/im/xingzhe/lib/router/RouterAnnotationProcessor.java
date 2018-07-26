package im.xingzhe.lib.router;

import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import im.xingzhe.lib.router.annotation.DefaultDestination;
import im.xingzhe.lib.router.annotation.DestinationArgument;
import im.xingzhe.lib.router.annotation.DestinationUri;
import im.xingzhe.lib.router.annotation.UriDestination;


@AutoService(Processor.class)
public class RouterAnnotationProcessor extends AbstractProcessor {

    static final String ROOT_PACKAGE = "im.xingzhe.lib.router";
    static final String PACKAGE_DESTINATION = ROOT_PACKAGE + ".destination";
    static final String PACKAGE_DESTINATION_IMPL = PACKAGE_DESTINATION + ".impl";

    //im.xingzhe.lib.router.destination.DestinationDefinition
    final ClassName DESTINATION_DEFINITION = ClassName.get(PACKAGE_DESTINATION_IMPL, "DestinationDefinition");
    final ParameterizedTypeName DESTINATION_DEFINITION_ARRAY_LIST = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(PACKAGE_DESTINATION_IMPL, "DestinationDefinition"));


    //im.xingzhe.lib.router.destination.UriDestinationArgumentDefinition
    final ClassName URI_DESTINATION_DEFINITION = ClassName.get(PACKAGE_DESTINATION_IMPL, "UriDestinationDefinition");
    final ParameterizedTypeName URI_DESTINATION_DEFINITION_ARRAY_LIST = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(PACKAGE_DESTINATION_IMPL, "DestinationDefinition"));
    final ParameterizedTypeName URI_DESTINATION_DEFINITION_LIST = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(PACKAGE_DESTINATION_IMPL, "DestinationDefinition"));


    final ClassName DESTINATION_ARGUMENT_DEFINITION = ClassName.get(PACKAGE_DESTINATION_IMPL, "DestinationArgumentDefinition");
    final ParameterizedTypeName DESTINATION_ARGUMENT_DEFINITION_ARRAY_LIST = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(PACKAGE_DESTINATION_IMPL, "DestinationArgumentDefinition"));
    final ParameterizedTypeName DESTINATION_ARGUMENT_DEFINITION_LIST = ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(PACKAGE_DESTINATION, "DestinationArgumentDefinition"));


    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    /**
     * 元素相关的辅助类
     */
    private Elements mElementUtils;
    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;

    private Types mTypes;


    /**
     * 每一个注解处理器类都必须有一个空的构造函数。
     * 然而，这里有一个特殊的init()方法，它会被注解处理工具调用，
     * 并输入ProcessingEnviroment参数。
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mTypes = processingEnvironment.getTypeUtils();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> elementsWithUriDestination = roundEnvironment.getElementsAnnotatedWith(UriDestination.class);
        Set<? extends Element> elementsWithDestination = roundEnvironment.getElementsAnnotatedWith(DefaultDestination.class);

        if (elementsWithDestination.isEmpty() && elementsWithUriDestination.isEmpty()) {
            return false;
        }

        Set<Element> annotationElements = Sets.newHashSet(elementsWithDestination);
        annotationElements.addAll(elementsWithUriDestination);

        generateDestinationService(annotationElements);

        return true;
    }

    private void generateDestinationArgumentClasses(UriDestination destination) {

        DestinationArgument[] inArgs = destination.in();
        DestinationArgument[] outArgs = destination.out();


        generateDestinationArgumentOnce(destination, inArgs, "In");
        generateDestinationArgumentOnce(destination, outArgs, "Out");

    }

    private void generateDestinationArgumentOnce(UriDestination destination, DestinationArgument[] args, String posfix) {
        if (args == null || args.length == 0) {
            return;
        }

        CaseFormat caseFormat = Utils.resolveCaseFormat(destination.name());

        //android.os.Bundle
        TypeName bundleType = ClassName.get("android.os", "Bundle");
        //android.os.Parcelable
        TypeName parcelableType = ClassName.get("android.os", "Parcelable");

        //im.xingzhe.lib.router.action.ActionArguments
        String targetPackage = "im.xingzhe.lib.router.action.args";
        TypeName actionArguments = ClassName.get("im.xingzhe.lib.router.action", "ActionArguments");
        String simpleName = caseFormat.to(CaseFormat.UPPER_CAMEL, destination.name()) + "Arguments" + posfix;
        TypeSpec.Builder argumentsClass = TypeSpec
                .classBuilder(simpleName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addSuperinterface(actionArguments);


        MethodSpec.Builder toBundleMethodBuilder = MethodSpec.methodBuilder("toBundle")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(
                        ParameterSpec.builder(ClassName.get(targetPackage, simpleName), "args", Modifier.FINAL)

                                //android.support.annotation.Nullable
                                .addAnnotation(ClassName.get("android.support.annotation", "Nullable"))
                                .build()
                )
                .addStatement("final $T bundle = new $T()", bundleType, bundleType)
                .returns(bundleType);

        MethodSpec.Builder fromBundleMethodBuilder = MethodSpec.methodBuilder("fromBundle")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(
                        ParameterSpec.builder(bundleType, "bundle", Modifier.FINAL)

                                //android.support.annotation.Nullable
                                .addAnnotation(ClassName.get("android.support.annotation", "Nullable"))
                                .build()
                )
                .addStatement("final $T args = new $T()", ClassName.get(targetPackage, simpleName), ClassName.get(targetPackage, simpleName))
                .returns(ClassName.get(targetPackage, simpleName));

        //生成getter/setter
        toBundleMethodBuilder.beginControlFlow("if(args != null)");
        fromBundleMethodBuilder.beginControlFlow("if(bundle != null)");
        for (DestinationArgument argument : args) {
            String key = argument.key();
            CaseFormat argumentNameCaseFormat = Utils.resolveCaseFormat(key);

            TypeMirror typeMirror = getArgumentType(argument);
            TypeName argumentType = ClassName.get(typeMirror); // ClassName.get(mElementUtils.getPackageOf(mTypes.asElement(typeMirror)).getQualifiedName().toString(), mTypes.asElement(typeMirror).getSimpleName().toString() );

            boolean isBooleanType = typeMirror.getKind() == TypeKind.BOOLEAN || mTypes.isAssignable(typeMirror, mTypes.getDeclaredType(mElementUtils.getTypeElement(Boolean.class.getCanonicalName())));
            String propertyName = argumentNameCaseFormat.to(CaseFormat.LOWER_CAMEL, argument.key());
            String getterName = (isBooleanType ? "is" : "get") + argumentNameCaseFormat.to(CaseFormat.UPPER_CAMEL, argument.key());
            String setName = "set" + argumentNameCaseFormat.to(CaseFormat.UPPER_CAMEL, argument.key());

            FieldSpec propertySpec = FieldSpec
                    .builder(argumentType, propertyName, Modifier.PRIVATE)
                    .addJavadoc("Generated property for $L", key)
                    .build();


            MethodSpec.Builder getter = MethodSpec.methodBuilder(getterName)
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .addStatement("return $N", propertySpec)
                    .returns(argumentType);

            MethodSpec.Builder setter = MethodSpec.methodBuilder(setName)
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .addParameter(
                            ParameterSpec.builder(argumentType, propertyName)
                                    .build()
                    )
                    .addStatement("this.$N = $L", propertySpec, propertyName)
                    .addStatement("return this", propertySpec)
                    .returns(ClassName.get(targetPackage, simpleName));


            addBundleStatements(toBundleMethodBuilder, fromBundleMethodBuilder, propertySpec, argument);
            argumentsClass.addField(propertySpec);
            argumentsClass.addMethod(getter.build());
            argumentsClass.addMethod(setter.build());
        }

        toBundleMethodBuilder.endControlFlow();
        fromBundleMethodBuilder.endControlFlow();

        toBundleMethodBuilder.addStatement("return bundle");
        fromBundleMethodBuilder.addStatement("return args");
        argumentsClass.addMethod(toBundleMethodBuilder.build());
        argumentsClass.addMethod(fromBundleMethodBuilder.build());

        argumentsClass.addMethod(
                MethodSpec.methodBuilder("toBundle")
                        .returns(bundleType)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return toBundle(this)")
                        .build()

        );

        JavaFile javaFile = JavaFile.builder(targetPackage, argumentsClass.build())
                .build();


        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addCheckStatements(MethodSpec.Builder toBundleMethodBuilder, MethodSpec.Builder fromBundleMethodBuilder, FieldSpec propertySpec, DestinationArgument destinationArgument) {
        TypeName argumentType = TypeName.get(getArgumentType(destinationArgument));

        if (!argumentType.isPrimitive()) {
            if (destinationArgument.require()) {
                toBundleMethodBuilder.addStatement("if(args.$N == null) throw new $T($S)", propertySpec, ClassName.get(IllegalStateException.class), String.format("Argument %s is required", destinationArgument.key()));
            }
        }


    }

    private void addBundleStatements(MethodSpec.Builder toBundleMethodBuilder, MethodSpec.Builder fromBundleMethodBuilder, FieldSpec propertySpec, DestinationArgument destinationArgument) {
        TypeName argumentType = TypeName.get(getArgumentType(destinationArgument));
        String key = destinationArgument.key();
        //检查参数

        if (argumentType.isPrimitive() || argumentType.isBoxedPrimitive()) {
            boolean isPrimitive = argumentType.isPrimitive();
            argumentType = argumentType.unbox();
            if (argumentType.equals(TypeName.BOOLEAN)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putByte($S, args.$N ? 1 : 0)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putByte($S, args.$N ? 1 : 0)", propertySpec, key, propertySpec);
                }

                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getByte($S) != 0", key, propertySpec, key);
            } else if (argumentType.equals(TypeName.BYTE)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putByte($S, args.$N)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putByte($S, args.$N)", propertySpec, key, propertySpec);
                }

                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getByte($S)", key, propertySpec, key);
            } else if (argumentType.equals(TypeName.SHORT)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putShort($S, args.$N)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putShort($S, args.$N)", propertySpec, key, propertySpec);
                }

                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getShort($S)", key, propertySpec, key);
            } else if (argumentType.equals(TypeName.INT)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putInt($S, args.$N)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putInt($S, args.$N)", propertySpec, key, propertySpec);
                }

                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getInt($S)", key, propertySpec, key);
            } else if (argumentType.equals(TypeName.LONG)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putLong($S, args.$N)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putLong($S, args.$N)", propertySpec, key, propertySpec);
                }

                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getLong($S)", key, propertySpec, key);
            } else if (argumentType.equals(TypeName.CHAR)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putChar($S, args.$N)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putChar($S, args.$N)", propertySpec, key, propertySpec);
                }

                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getChar($S)", key, propertySpec, key);
            } else if (argumentType.equals(TypeName.FLOAT)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putFloat($S, args.$N)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putFloat($S, args.$N)", propertySpec, key, propertySpec);
                }


                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getFloat($S)", key, propertySpec, key);
            } else if (argumentType.equals(TypeName.DOUBLE)) {
                if (isPrimitive) {
                    toBundleMethodBuilder.addStatement("bundle.putDouble($S, args.$N)", key, propertySpec);
                } else {
                    toBundleMethodBuilder.addStatement("if(args.$N != null) bundle.putDouble($S, args.$N)", propertySpec, key, propertySpec);
                }

                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getDouble($S)", key, propertySpec, key);
            }
        } else if (argumentType.equals(ClassName.get(String.class))) {
            toBundleMethodBuilder.addStatement("if(args.$N != null)  bundle.putString($S, args.$N)", propertySpec, key, propertySpec);
            fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = bundle.getString($S)", key, propertySpec, key);
        } else if (argumentType instanceof ClassName) {
            TypeMirror argumentTypeMirror = mElementUtils.getTypeElement(((ClassName) argumentType).reflectionName()).asType();
            TypeMirror parcelableTypeMirror = mElementUtils.getTypeElement("android.os.Parcelable").asType();

            if (mTypes.isAssignable(argumentTypeMirror, parcelableTypeMirror)) {
                toBundleMethodBuilder.addStatement("if(args.$N != null)   bundle.putParcelable($S, args.$N)", propertySpec, key, propertySpec);
                fromBundleMethodBuilder.addStatement("if(bundle.containsKey($S)) args.$N = ($T) bundle.getParcelable($S)", key, propertySpec, argumentType, key);
            }
        }

    }

    private void generateDestinationService(Set<Element> annotationElements) {

        FieldSpec definitions = FieldSpec
                .builder(DESTINATION_DEFINITION_ARRAY_LIST, "definitions", Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", DESTINATION_DEFINITION_ARRAY_LIST)
                .build();


        MethodSpec.Builder constructMethodBuilder = MethodSpec.constructorBuilder();
        constructMethodBuilder.addModifiers(Modifier.PUBLIC);
        constructMethodBuilder.addStatement("$T inArguments = null", DESTINATION_ARGUMENT_DEFINITION_ARRAY_LIST);
        constructMethodBuilder.addStatement("$T outArguments = null", DESTINATION_ARGUMENT_DEFINITION_ARRAY_LIST);
        constructMethodBuilder.addStatement("$T uri = null", ClassName.get("android.net", "Uri"));


        for (Element element : annotationElements) {
            UriDestination uriDestination = element.getAnnotation(UriDestination.class);
            DefaultDestination defaultDestination = element.getAnnotation(DefaultDestination.class);

            if (uriDestination != null) {

                constructMethodBuilder.addStatement("inArguments = new $T()", DESTINATION_ARGUMENT_DEFINITION_ARRAY_LIST);
                constructMethodBuilder.addStatement("outArguments = new $T()", DESTINATION_ARGUMENT_DEFINITION_ARRAY_LIST);


                DestinationArgument[] inArgs = uriDestination.in();
                DestinationArgument[] outArgs = uriDestination.out();

                for (DestinationArgument argument : inArgs) {
                    constructMethodBuilder.addStatement("inArguments.add(new $T($S, $L, $T.class))",
                            DESTINATION_ARGUMENT_DEFINITION,
                            argument.key(),
                            argument.require(),
                            getArgumentType(argument)
                    );
                }


                for (DestinationArgument argument : outArgs) {
                    constructMethodBuilder.addStatement("outArguments.add(new $T($S, $L, $T.class))",
                            DESTINATION_ARGUMENT_DEFINITION,
                            argument.key(),
                            argument.require(),
                            getArgumentType(argument)
                    );
                }


                DestinationUri destinationUri = uriDestination.uri();
                String scheme = destinationUri.scheme();
                String authority = destinationUri.authority();
                String path = destinationUri.path();

                String uriForString = scheme + "://" + authority + (path.startsWith("/") ? path : "/" + path);
                constructMethodBuilder.addStatement("uri = $T.parse($S)",
                        ClassName.get("android.net", "Uri"),
                        uriForString);

                constructMethodBuilder.addStatement("definitions.add(new $T($S, $T.class, inArguments,  outArguments, uri))", URI_DESTINATION_DEFINITION, uriDestination.name(), element.asType());


                generateDestinationArgumentClasses(uriDestination);
            }

        }


        MethodSpec getDestinationDefinitionsMethodSpec = MethodSpec
                .methodBuilder("getDestinationDefinitions")
                .addModifiers(Modifier.PROTECTED)
                .addStatement("return this.$N", definitions)
                .returns(URI_DESTINATION_DEFINITION_LIST)
                .build();

        //im.xingzhe.lib.router.destination.impl.AbstractUriDestinationService
        ClassName abstractDestinationServiceTyp = ClassName.get("im.xingzhe.lib.router.destination.impl", "AbstractUriDestinationService");
        TypeSpec destinationService = TypeSpec
                .classBuilder("DestinationServiceImpl")
                .superclass(abstractDestinationServiceTyp)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(PACKAGE_DESTINATION, "DestinationService"))
                .addField(definitions)
                .addMethod(constructMethodBuilder.build())
                .addMethod(getDestinationDefinitionsMethodSpec)
                .build();


        JavaFile javaFile = JavaFile.builder(PACKAGE_DESTINATION_IMPL, destinationService)
                .build();


        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(
                UriDestination.class.getCanonicalName(),
                DefaultDestination.class.getCanonicalName()
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public static TypeMirror getArgumentType(DestinationArgument ag) {
        try {
            ag.type();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }

        return null;
    }
}
