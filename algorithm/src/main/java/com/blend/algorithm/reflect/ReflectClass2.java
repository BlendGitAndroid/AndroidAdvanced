package com.blend.algorithm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 反射的作用：
 * 反射之中包含了一个「反」字，所以了解反射我们先从「正」开始。
 * 一般情况下，我们使用某个类时必定知道它是什么类，是用来做什么的。于是我们直接对这个类进行实例化，之后使用这个类对象进行操作。
 * 反射则是一开始并不知道我要初始化的类对象是什么，自然也无法使用 new 关键字来创建对象了。这时候，我们使用 JDK 提供的反射 API
 * 进行反射调用。反射就是在运行时才知道要操作的类是什么，并且可以在运行时获取类的完整构造，并调用对应的方法。
 * <p>
 * 反射的概念：
 * Reflection（反射）是Java被视为动态语言的关键，反射机制允许程序在执行期借助于Reflection API取得任何类的內部信息，并能
 * 直接操作任意对象的内部属性及方法。
 * <p>
 * 作用：
 * 1.在运行时构造任意一个类的对象。
 * 2.在运行时获取任意一个类所具有的成员变量和方法。
 * 3.在运行时调用任意一个对象的方法（属性）。
 * <p>
 * 类对象
 * Java是一门面向对象的语言。在面向对象的世界里，万事万物皆对象，既然万事万物皆对象，那么我们写的每一个类都可以看成一个对象，
 * 是java.lang.Class类的对象。当我们写完一个类的Java文件，编译成class文件的时候，编译器都会将这个类的对应的class对象放
 * 在class文件的末尾。里面保存了类的元数据信息，有属性，方法，构造器，接口等等，这些信息在Java里都有对应的类来表示。
 * <p>
 * java.lang.Class类
 * Class是一个类，封装了当前对象所对应的类的信息。一个类中有属性，方法，构造器等，比如说有一个Person类，一个Order类，
 * 一个Book类，这些都是不同的类，现在需要一个类，用来描述类，这就是Class，它应该有类名，属性，方法，构造器等。Class是
 * 用来描述类的类。Class类是一个对象照镜子的结果，对象可以看到自己有哪些属性，方法，构造器，实现了哪些接口等等。对于每个类
 * 而言，JRE 都为其保留一个不变的 Class 类型的对象。一个 Class 对象包含了特定某个类的有关信息。对象只能由系统建立对象，
 * 一个类（而不是一个对象）在 JVM 中只会有一个Class实例。
 * <p>
 * 获取Class类中的构造器，字段和方法名等都是不需要newInstance，因为这是Class对象的基本信息，但是要获取到他的值，就需要了，
 * 因为需要一个对象调用相应的字段名和方法名。
 */
class ReflectClass2 {

    public static void main(String[] args) {
        try {
            /*类加载器相关*/
            testClassLoader();

            System.out.println("-------------------------");

            /*构造器相关*/
            testConstructor();

            System.out.println("-------------------------");

            /*字段相关*/
            testField();

            System.out.println("-------------------------");

            /*方法相关*/
            testMethod();

            System.out.println("-------------------------");

            /*泛型反射*/
            testGeneric();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //getType和getGenericType在不是泛型的时候返回都是一样的
    //但是如果是有泛型的情况下，会根据是否有getGenericSignature来判断
    //需要注意区分的是：获取泛型属性和泛型类是不同的，泛型属性直接通过Field#getGenericType()获取
    //获取泛型类，是需要通过自定义TypeToken，通过getGenericSuperclass获取
    private static void testGeneric() throws NoSuchFieldException {
        Class<Book> bookClass = Book.class;
        Field mStringList = bookClass.getDeclaredField("mStringList");
        //注意区分这里的Field#getType，返回到是Class对象
        Class<?> classType = mStringList.getType();
        System.out.println(classType);
        //isAssignableFrom是class对象
        final Class<List> listClass = List.class;
        // `isAssignableFrom`是Java中的一个方法，用于判断一个类是否是另一个类的子类或者实现了某个接口。
        if (listClass.isAssignableFrom(classType)) {
            //这里Field#getGenericType返回的是Type对象
            Type genericType = mStringList.getGenericType();
            System.out.println(genericType);
            //如果这个是参数化类型
            if (genericType instanceof ParameterizedType) {
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                Type getRawType = ((ParameterizedType) genericType).getRawType();
                Type getOwnerType = ((ParameterizedType) genericType).getOwnerType();
                System.out.println(actualTypeArgument);
                System.out.println(getRawType);
                System.out.println(getOwnerType);
            }
        }

        Type type = new MyTypeToken<List<String>>() {
        }.getType();

        System.out.println(type);
    }

    private static void testMethod() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<Book> bookClass = (Class<Book>) Class.forName("com.blend.algorithm.reflect.Book");
        //获取对应类中的所有方法，不能获取private方法，且获取从父类继承来的所有方法
        Method[] methods = bookClass.getMethods();
        for (Method method : methods) {
            System.out.print(method.getName() + " ");
        }
        System.out.println();

        //获取所有的方法，但只能获取到当前类的方法，不能获取父类的方法
        methods = bookClass.getDeclaredMethods();
        for (Method method : methods) {
            System.out.print(method.getName() + " ");
        }
        System.out.println();

        //获取指定的方法，需要参数名和参数列表，无参则不需要写
        Method setName = bookClass.getDeclaredMethod("setName", String.class);
        System.out.println(setName.getName());
        Book book = bookClass.getConstructor(String.class, String.class).newInstance("Android", "Blend");

        setName.invoke(book, "Blend++");
        System.out.println(book.getName() + " " + book.getAuthor());
    }

    private static void testField() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<Book> bookClass = (Class<Book>) Class.forName("com.blend.algorithm.reflect.Book");
        //获取公有和私有的所有字段，但不能获取父类字段
        Field[] declaredFields = bookClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.print(declaredField.getName() + " ");
        }
        System.out.println();

        //获取指定字段
        Field name = bookClass.getDeclaredField("name");
        System.out.println(name.getName());

        //获取指定字段的值
        Book book = new Book("Android", "Blend");
        name.setAccessible(true);
        Object o = name.get(book);
        System.out.println(name.getName() + ":" + o);

        //设置指定字段的值
        name.set(book, "Android++");
        o = name.get(book);
        System.out.println(name.getName() + ":" + o);
    }

    private static void testConstructor() throws Exception {
        Class<Book> bookClass = (Class<Book>) Class.forName("com.blend.algorithm.reflect.Book");
        //获取全部的构造器对象
        Constructor<Book>[] constructors = (Constructor<Book>[]) bookClass.getDeclaredConstructors();
        for (Constructor<Book> constructor : constructors) {
            System.out.println(constructor);
        }
    }

    private static void testClassLoader() throws ClassNotFoundException {
        //1.获取一个系统的类加载器(可以获取，当前这个类就是它加载的)
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        System.out.println(classLoader);

        //2.获取系统类加载器的父类加载器（可以获取，扩展类加载器）
        classLoader = classLoader.getParent();
        System.out.println(classLoader);

        //3.获取扩展类加载器的父类加载器(不可获取，引导类加载器)
        classLoader = classLoader.getParent();
        System.out.println(classLoader);

        //4.测试当前类是由哪个类加载器加载的（系统类加载器）
        classLoader = Class.forName("com.blend.algorithm.reflect.ReflectClass2").getClassLoader();
        System.out.println(classLoader);

        //5.Object类是由哪个类加载器加载的（引导类）
        classLoader = Class.forName("java.lang.Object").getClassLoader();
        System.out.println(classLoader);
    }

    //这里就是写一个抽象类，但是这个抽象类是泛型的，让子类实现这个抽象类，传入需要解析的泛型javabean，
    // 然后通过getGenericsSuperclass()就能获取到这个泛型信息了。
    public static abstract class MyTypeToken<T> {
        private final Type type;

        //因为这里是抽象类，所以使用的是getGenericSuperclass
        protected MyTypeToken() {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            type = typeArguments[0];
        }

        public Type getType() {
            return type;
        }
    }
}
