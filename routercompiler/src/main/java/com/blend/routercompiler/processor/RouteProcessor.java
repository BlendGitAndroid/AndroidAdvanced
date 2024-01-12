package com.blend.routercompiler.processor;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.blend.routerannotation.Route;
import com.blend.routerannotation.model.RouteMeta;
import com.blend.routercompiler.utils.Consts;
import com.blend.routercompiler.utils.Log;
import com.blend.routercompiler.utils.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 编译器注解的核心原理依赖APT（Annotation Processing Tools）实现，著名的ButterKnife、Dagger、等开源库都是基于APT。
 * 基本原理是在某些代码元素上（如类型、函数、字段）添加注解，在编译时编译器会检查AbstractProcessor的子类，并且调用该类型的process
 * 函数，将添加了注解的所有元素都传递到process函数中，使得开发人员可以在编译期进行相应的处理，例如根据注解生成新的java类，在编译时生
 * 成一些重复性操作的Java代码，或者不需要程序员去关心的Java代码等。
 * <p>
 * 对于编译器来说，代码中的元素结构是基本不变的，例如组成代码的基本元素有包，类，函数，字段，类型参数，变量等。JDK为这些元素定义了一个
 * 基类，也就是Element类，他有如下几个子类：
 * 1）PackageElement：包元素，包含了某个包下的信息，可以获取到包名等。表示包元素，提供对有关包及其成员的信息的访问
 * 2）TypeElement：类型元素，如某个字段属于某种类型，表示类、接口元素。提供对有关类型及其成员的信息的访问
 * 3）ExecutableElement：可执行元素，代表了函数类型的元素，表示类、接口的方法元素。包括构造方法、注解类型
 * 4）VariableElement：变量元素，表示字段、enum、方法、构造方法参数、局部变量、异常参数
 * 5）TypeParameterElement：类型参数元素。表示类、接口、方法、构造方法的参数元素
 * 因为注解可以指定作用到哪些元素上，因此，通过上述的抽象来对应这些元素。
 */

/**
 * 在这个类上添加了@AutoService注解，它的作用是用来生成META-INF/services/javax.annotation.processing.Processor文件的，
 * 也就是我们在使用注解处理器的时候需要手动添加META-INF/services/javax.annotation.processing.Processor，
 * 而有了@AutoService后它会自动帮我们生成。AutoService是Google开发的一个库，使用时需要在build中添加依赖。
 * <p>
 * 这个库的主要作用是注册注解，并对其生成META-INF的配置信息
 */
@AutoService(Processor.class)
/**
 * 处理器接收的参数 替代 {@link AbstractProcessor#getSupportedOptions()} 函数
 * 通过getOptions方法获取选项参数值。
 */
@SupportedOptions(Consts.ARGUMENTS_NAME)
/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()} 函数
 * 声明我们注解支持的JDK的版本
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**
 * 注册给哪些注解的  替代 {@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 * 声明我们要处理哪一些注解 该方法返回字符串的集合表示该处理器用于处理哪些注解
 */
@SupportedAnnotationTypes({Consts.ANN_TYPE_ROUTE})
public class RouteProcessor extends AbstractProcessor {

    /**
     * key:模块名 value:该模块下生成的的路由类名
     */
    private final Map<String, String> rootMap = new TreeMap<>();
    /**
     * 分组 key:group名字 value:对应组的路由信息集合
     */
    private final Map<String, List<RouteMeta>> groupMap = new HashMap<>();
    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;

    /**
     * type(类信息)工具类
     */
    private Types typeUtils;
    /**
     * 文件生成器 类/资源
     */
    private Filer filerUtils;
    /**
     * 参数
     */
    private String moduleName;

    private Log log;

    /**
     * 初始化 从 {@link ProcessingEnvironment} 中获得一系列处理器工具，为后面的注解处理提供帮助
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //获得apt的日志输出
        log = Log.newLog(processingEnvironment.getMessager());  //返回实现Messager接口的对象，用于报告错误信息、警告提醒。
        log.i("RouteProcessor init()");
        elementUtils = processingEnvironment.getElementUtils(); //返回实现Elements接口的对象，用于处理元素的工具类。
        typeUtils = processingEnvironment.getTypeUtils();   //返回实现Types接口的对象，用于处理类型的工具类。
        filerUtils = processingEnvironment.getFiler();  //返回实现Filer接口的对象，用于创建文件、类和辅助文件。
        //参数是模块名 为了防止多模块/组件化开发的时候 生成相同的 xx$$ROOT$$文件
        //返回指定的额外参数选项，在@SupportedOptions注解中有赋值，这个值取的是build.gradle中配置的，一般是module的名字
        Map<String, String> options = processingEnvironment.getOptions();
        if (!Utils.isEmpty(options)) {
            moduleName = options.get(Consts.ARGUMENTS_NAME);
        }
        log.i("RouteProcessor Parameters:" + moduleName);
        if (Utils.isEmpty(moduleName)) {
            throw new RuntimeException("Not set Processor Parameters.");
        }
    }

    /**
     * 相当于main函数，正式处理注解，获取注解的元素，对元素进行额外处理，可用JavaPoet生成Java代码
     *
     * @param set              使用了支持处理注解的类型元素的集合
     * @param roundEnvironment 表示当前或是之前的运行环境,可以通过该对象查找到的注解。
     * @return true 表示后续处理器不会再处理(已经处理)，如果返回 false，则这些注解未在此Processor中处理并，
     * 那么后续 Processor 可以继续处理它们。
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //使用了需要处理的注解
        if (!Utils.isEmpty(set)) {
            //获取所有被 Route 注解的元素集合
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
            //处理 Route 注解
            if (!Utils.isEmpty(routeElements)) {
                try {
                    log.i("Route Class size: " + routeElements.size());
                    parseRoutes(routeElements);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }


    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        //支持配置路由类的类型
        //这一步主要是判断注解上的是activity和service
        TypeElement activity = elementUtils.getTypeElement(Consts.ACTIVITY);
        //节点自描述 Mirror，表示Java编程语言中的类型。这些类型包括基本类型、引用类型、数组类型、类型变量和null类型等等
        TypeMirror type_Activity = activity.asType();
        log.i("Route Class type: " + type_Activity);
        TypeElement iService = elementUtils.getTypeElement(Consts.ISERVICE);
        TypeMirror type_IService = iService.asType();


        /**
         * groupMap(组名:路由信息)集合
         */
        //声明 Route 注解的节点 (需要处理的节点 Activity/IService)
        for (Element element : routeElements) {
            //路由信息
            RouteMeta routeMeta;
            // 使用Route注解的类信息
            TypeMirror tm = element.asType();
            log.i("Route Class TypeMirror: " + tm.toString());
            Route route = element.getAnnotation(Route.class);
            //是否是 Activity 使用了Route注解
            if (typeUtils.isSubtype(tm, type_Activity)) {
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, route, element);
            } else if (typeUtils.isSubtype(tm, type_IService)) {
                routeMeta = new RouteMeta(RouteMeta.Type.ISERVICE, route, element);
            } else {
                throw new RuntimeException("[Just Support Activity/IService Route] :" + element);
            }
            //分组信息记录  groupMap <Group分组,RouteMeta路由信息> 集合
            categories(routeMeta);
        }

        //生成类需要实现的接口
        //下面是用来判断是否实现了group或者root接口
        TypeElement iRouteGroup = elementUtils.getTypeElement(Consts.IROUTE_GROUP);
        log.i("iRouteGroup getSimpleName: " + iRouteGroup.getSimpleName());
        TypeElement iRouteRoot = elementUtils.getTypeElement(Consts.IROUTE_ROOT);

        /**
         *  生成Group类 作用:记录 <地址,RouteMeta路由信息(Class文件等信息)>
         */
        generatedGroup(iRouteGroup);
        /**
         * 生成Root类 作用:记录 <分组，对应的Group类>
         */
        generatedRoot(iRouteRoot, iRouteGroup);
    }

    // 生成 $$Group$$ 类文件
    // public class BlendRouter$$Group$$module1 implements IRouteGroup {
    //   @Override
    //   public void loadInto(Map<String, RouteMeta> atlas) {
    //     atlas.put("/module1/test", RouteMeta.build(RouteMeta.Type.ACTIVITY,RouterModule1MainActivity.class, "/module1/test", "module1"));
    //     atlas.put("/module1/service", RouteMeta.build(RouteMeta.Type.ISERVICE,TestServiceImpl.class, "/module1/service", "module1"));
    //   }
    // }
    // 生成 $$Root$$ 类文件
    //public class BlendRouter$$Root$$routermodule1 implements IRouteRoot {
    //   @Override
    //   public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
    //     routes.put("module1", BlendRouter$$Group$$module1.class);
    //   }
    // }

    // 1.先构建函数参数
    // 2.构建函数签名
    // 3.构建函数体
    // 4.构建类名
    // 5.构建类

    //这里的iRouteGroup就是一个接口
    private void generatedGroup(TypeElement iRouteGroup) throws IOException {

        // `ParameterizedTypeName`是JavaPoet库中的一个类，用于表示带有参数化类型的类型。
        // `public static ParameterizedTypeName get(ClassName rawType, TypeName... typeArguments)`：创建一个
        // 参数化类型的`ParameterizedTypeName`对象，其中泛型参数是`TypeName`类型。
        // 下面代码表示: Map<String,RouteMeta>
        ParameterizedTypeName atlas = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );

        // `ParameterSpec`是JavaPoet库中的一个类，用于表示方法或构造函数的参数。
        // 在Java中，方法和构造函数可以接受零个或多个参数。`ParameterSpec`类的作用是允许我们在代码生成过程中，表示和操作方法
        // 或构造函数的参数。它提供了一些方法用于创建和操作参数，如获取参数名称、获取参数类型、获取参数修饰符等。
        // 下面代码表示: Map<String,RouteMeta> atlas
        ParameterSpec groupParamSpec = ParameterSpec.builder(atlas, "atlas").build();

        //遍历分组,每一个分组创建一个 $$Group$$ 类
        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            /**
             * 类成员函数loadInto声明构建
             */
            //函数签名 public void loadInto(Map<String,RouteMeta> atlas)
            MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(groupParamSpec);

            //分组名 与 对应分组中的信息
            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            //遍历分组中的条目 数据
            for (RouteMeta routeMeta : groupData) {
                // 组装函数体:
                // atlas.put(地址,RouteMeta.build(Class,path,group))
                loadIntoMethodOfGroupBuilder.addStatement(
                        "atlas.put($S, $T.build($T.$L,$T.class, $S, $S))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        routeMeta.getType(),
                        ClassName.get((TypeElement) routeMeta.getElement()),
                        routeMeta.getPath().toLowerCase(),
                        routeMeta.getGroup().toLowerCase());
            }
            // 创建java文件($$Group$$)  组
            String groupClassName = Consts.NAME_OF_GROUP + groupName;
            JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE,  //包名
                            TypeSpec.classBuilder(groupClassName)   //类名
                                    .addSuperinterface(ClassName.get(iRouteGroup))  //实现IRouteGroup接口
                                    .addModifiers(PUBLIC)
                                    .addMethod(loadIntoMethodOfGroupBuilder.build())
                                    .build())
                    .build().writeTo(filerUtils);
            log.i("Generated RouteGroup: " + Consts.PACKAGE_OF_GENERATE_FILE + "." + groupClassName);
            //分组名和生成的对应的Group类类名
            rootMap.put(groupName, groupClassName);
        }
    }

    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) throws IOException {
        //Wildcard 表示通配符
        //类型 Map<String,Class<? extends IRouteGroup>>
        ParameterizedTypeName routes = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup)))
        );

        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec rootParamSpec = ParameterSpec.builder(routes, "routes").build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(rootParamSpec);

        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)",
                    entry.getKey(),
                    ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE, entry.getValue()));  //构建全类名
        }
        //生成 $Root$类
        String rootClassName = Consts.NAME_OF_ROOT + moduleName;
        JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootClassName)
                        .addSuperinterface(ClassName.get(iRouteRoot))
                        .addModifiers(PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .build()
        ).build().writeTo(filerUtils);

        log.i("Generated RouteRoot: " + Consts.PACKAGE_OF_GENERATE_FILE + "." + rootClassName);
    }

    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            log.i("Group Info, Group Name = " + routeMeta.getGroup() + ", Path = " + routeMeta.getPath());
            List<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            //如果未记录分组则创建
            if (Utils.isEmpty(routeMetas)) {
                List<RouteMeta> routeMetaSet = new ArrayList<>();
                routeMetaSet.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
            log.i("Group Info Error: " + routeMeta.getPath());
        }
    }

    /**
     * 验证路由信息必须存在path(并且设置分组)
     *
     * @param meta raw meta
     */
    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();
        String group = meta.getGroup();
        //路由地址必须以 / 开头
        if (Utils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }
        //如果没有设置分组,以第一个 / 后的节点为分组(所以必须path两个/)
        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (Utils.isEmpty(defaultGroup)) {
                return false;
            }
            meta.setGroup(defaultGroup);
            return true;
        }
        return true;
    }
}
