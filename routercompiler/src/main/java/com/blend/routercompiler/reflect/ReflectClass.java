package com.blend.routercompiler.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectClass {


    /**
     * JAVA反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意
     * 方法和属性；这种动态获取信息以及动态调用对象方法的功能称为java语言的反射机制。
     * <p>
     * <p>
     * 用途：
     * 在日常的第三方应用开发过程中，经常会遇到某个类的某个成员变量、方法或是属性是私有的或是只对系统应用开放，这时候就可以
     * 利用Java的反射机制通过反射来获取所需的私有成员或是方法。当然，也不是所有的都适合反射，之前就遇到一个案例，通过反射
     * 得到的结果与预期不符。阅读源码发现，经过层层调用后在最终返回结果的地方对应用的权限进行了校验，对于没有权限的应用返
     * 回值是没有意义的缺省值，否则返回实际值起到保护用户的隐私目的。
     * <p>
     * <p>
     * 反射的原理：获取方法
     * 在调用反射时，首先会创建 Class 对象，然后获取其 Method 对象，调用 invoke 方法，有两个方法，getMethod 和 getDeclaredMethod，
     * 流程分三步走：检查方法权限->获取方法Method对象->返回方法的拷贝。
     * 主要区别是：
     * 1)getMethod中的checkMemberAccess传入的是Member.PUBLIC(包括所有的 public 方法，包括父类的方法)，
     * 而getDeclaredMethod传入的是Member.DECLARED(包括所有自己定义的方法，public，protected，private 都在此，但是不包括父类的方法)。
     * 2)getMethod中获取方法调用的是getMethod0，而getDeclaredMethod获取方法调用的是privateGetDeclaredMethods，而 getMethod0会
     * 递归查找父类的方法，其中会调用到 privateGetDeclaredMethods 方法。
     * <p>
     * 流程详细解析：
     * 1.检查方法权限：对于非Member.PUBLIC的访问，会增加一项运行时权限。
     * 2.获取方法的Method对象：主要解析getMethod0，getMethod0主要调用的是getMethodsRecursive方法返回一个MethodList对象，是一个链表
     * 结点，也就是会获取到多个Method。解释一下在我们平时编写 Java 代码时，同一个类是不能有方法名和方法参数都相同的方法的，而实际上，
     * 在 JVM 中，一个方法签名是和 返回值，方法名，方法参数 三者相关的。也就是说，在 JVM 中，可以存在 方法名和方法参数都相同，但是返回值不
     * 同的方法。
     * 返回一个方法链表，主要通过四个步骤：
     * 1)通过 privateGetDeclaredMethods 获取自己所有的 public 方法。首先通过reflectionData通过缓存获取，维护一个reflectionData的
     * 软引用，保存了Class中的属性和方法；如果缓存为空，就会通过getDeclaredMethods0 从 JVM 中查找方法。
     * 2)通过 MethodList#filter 查找 方法名，参数相同的方法，如果找到，直接返回。
     * 3)如果自己没有实现对应的方法，就去父类中查找对应的方法。
     * 4)查找接口中对应的方法。
     * 3.返回方法的拷贝：两点要注意，
     * 一是：设置 root = this。
     * 二是：会给 Method 设置 MethodAccessor，用于后面方法调用。也就是所有的 Method 的拷贝都会使用同一份 methodAccessor，
     * methodAccessor对象具体就是生成反射类的入口。
     * <p>
     * <p>
     * 反射的原理：调用反射方法，获取到方法后，通过Method#invoke调用方法，分为三步：
     * 1.检查是否有权限调用方法。
     * 2.获取MethodAccessor。一共有三种 MethodAccessor，MethodAccessorImpl、NativeMethodAccessorImpl和DelegatingMethodAccessorImpl。
     * 默认会调用 NativeMethodAccessorImpl。MethodAccessorImpl 是通过动态生成字节码来进行方法调用的，是 Java 版本的 MethodAccessor；
     * DelegatingMethodAccessorImpl 就是单纯的代理，真正的实现还是 NativeMethodAccessorImpl。
     * 在 NativeMethodAccessorImpl 的实现中，可以看到，有一个 numInvocations 阀值控制，numInvocations 表示调用次数。如果 numInvocations
     * 大于 15（默认阀值是 15），那么就使用 Java 版本的 MethodAccessorImpl。
     * 为什么采取这个策略呢？
     * Java 版本的 MethodAccessorImpl 调用效率比 Native 版本要快 20 倍以上，但是 Java 版本加载时要比 Native 多消耗 3-4 倍资源，所以默认会调
     * 用 Native 版本，如果调用次数超过 15 次以后，就会选择运行效率更高的 Java 版本。
     * 那为什么 Native 版本运行效率会没有 Java 版本高呢？是因为这是HotSpot的优化方式带来的性能特性，同时也是许多虚拟机的共同点：跨越native边界会对
     * 优化有阻碍作用，它就像个黑箱一样让虚拟机难以分析也将其内联，于是运行时间长了之后反而是托管版本的代码更快些。
     * 3.MethodAccessor#invoke 实现方法的调用。在生成 MethodAccessor 以后，就调用其 invoke 方法进行最终的反射调用。
     * <p>
     * <p>
     * Java反射效率低主要原因是：
     * 1.Method#invoke 方法会对参数做封装和解封操作。invoke方法的参数是 Object[] 类型，也就是说，如果方法参数是简单类型的话，需要在此转化成 Object 类型，
     * 例如 long ,在 javac compile 的时候 用了Long.valueOf() 转型，也就大量了生成了Long 的 Object, 同时 传入的参数是Object[]数值,那还需要额外封装
     * object数组。而在MethodAccessorGenerator#emitInvoke 方法里看到，生成的字节码时，会把参数数组拆解开来，把参数恢复到没有被 Object[] 包装前的样子，
     * 同时还要对参数做校验，这里就涉及到了解封操作。因此，在反射调用的时候，因为封装和解封，产生了额外的不必要的内存浪费，当调用次数达到一定量的时候，还会导致 GC。
     * 2.需要检查方法可见性。通过上面的源码分析，反射时每次调用都必须检查方法的可见性（在 Method.invoke 里）
     * 3.需要校验参数。反射时也必须检查每个实际参数与形式参数的类型匹配性（在NativeMethodAccessorImpl.invoke0里或者生成的 Java版MethodAccessor.invoke里）
     * 4.反射方法难以内联。Method#invoke就像是个独木桥一样，各处的反射调用都要挤过去，在调用点上收集到的类型信息就会很乱，影响内联程序的判断，
     * 使得 Method.invoke() 自身难以被内联到调用方。
     * 5.JIT(即时编译器)无法优化。因为反射涉及到动态加载的类型，所以编译器无法对反射相关的代码进行优化。Java 代码是需要编译才能在虚拟机里运行的，但其实 Java 的编译
     * 期是一段不确定的操作过程。因为它可能是一个前端编译器（如 Javac）把 *.java 文件编译成 *.class 文件的过程；也可能是程序运行期的即时编译器（JIT 编译器，
     * Just In Time Compiler）把字节码文件编译成机器码的过程；还可能是静态提前编译器（AOT 编译器，Ahead Of Time Compiler）直接把 *.java 文件编译成本地机器
     * 码的过程。即时编译器（JIT）在运行期的优化过程对于程序运行来说更重要，Java虚拟机在编译阶段的代码优化就在这里进行，由于反射涉及动态解析的类型，因此无法执行某些
     * Java虚拟机优化。因此，反射操作的性能要比非反射操作慢，因此应该避免在对性能敏感的应用程序中频繁使用Java反射来创建对象。
     */

    private static final String TAG = "ReflectClass";

    //创建对象方式1,一般使用第一种方式
    public static void reflectNewInstance() {
        try {
            Class<?> classBook = Class.forName("com.blend.routercompiler.reflect.Book");
            Object objectBook = classBook.newInstance();
            Book book = (Book) objectBook;
            book.setAuthor("11111");
            System.out.println("reflect create obj: " + book.getAuthor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建对象方式2
    public static void reflectNewInstance2() {
        try {
            Class<Book> bookClass = Book.class;
            Book book = bookClass.newInstance();
            book.setAuthor("22222");
            System.out.println("reflect create obj2: " + book.getAuthor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建对象方式3
    public static void reflectNewInstance3() {
        try {
            Book book = new Book();
            Class<? extends Book> aClass = book.getClass();
            Book reflectBook = aClass.newInstance();
            reflectBook.setAuthor("3333");
            System.out.println("reflect create obj3: " + reflectBook.getAuthor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //反射私有的构造方法
    public static void reflectPrivateConstractor() {
        try {
            Class<?> classBook = Class.forName("com.blend.routercompiler.reflect.Book");
            Constructor<?> declaredConstructor = classBook.getDeclaredConstructor(String.class, String.class);
            declaredConstructor.setAccessible(true);
            Object o = declaredConstructor.newInstance("Android study", "blend");
            Book book = (Book) o;
            System.out.println("reflect private constructor: " + book.getAuthor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //反射私有属性
    public static void reflectPrivateField() {
        try {
            Class<?> aClass = Class.forName("com.blend.routercompiler.reflect.Book");
            Object o = aClass.newInstance();
            Book book = (Book) o;
            book.setAuthor("private field");
            Field authorField = aClass.getDeclaredField("author");
            authorField.setAccessible(true);
            String author = (String) authorField.get(o);
            System.out.println("reflect private field: " + author);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //反射私有方法
    public static void reflectPrivateMethod() {
        try {
            Class<?> aClass = Class.forName("com.blend.routercompiler.reflect.Book");
            Method declaredMethod = aClass.getDeclaredMethod("declaredMethod", int.class);
            declaredMethod.setAccessible(true);
            Object o = aClass.newInstance();
            String s = (String) declaredMethod.invoke(o, 0);
            System.out.println("reflect private method: " + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        reflectNewInstance();
        reflectNewInstance2();
        reflectNewInstance3();
        reflectPrivateConstractor();
        reflectPrivateField();
        reflectPrivateMethod();
    }

}
