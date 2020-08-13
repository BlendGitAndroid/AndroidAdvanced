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
     */

    private static final String TAG = "ReflectClass";

    //创建对象
    public static void reflectNewInstance() {
        try {
            Class<?> classBook = Class.forName("com.blend.routercompiler.reflect.Book");
            Object objectBook = classBook.newInstance();
            Book book = (Book) objectBook;
            book.setAuthor("blend");
            System.out.println("reflect create obj: " + book.getAuthor());
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
        reflectPrivateConstractor();
        reflectPrivateField();
        reflectPrivateMethod();
    }

}
