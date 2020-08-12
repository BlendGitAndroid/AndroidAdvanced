package com.blend.routercompiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Java8Feature {


    public Java8Feature() {

    }

    /**
     * 接口的默认方法
     * 允许我们给接口添加一个非抽象的方法实现，只需要使用 default关键字即可，这个特征又叫做扩展方法
     */
    interface Formula {
        double calculate(int a);

        default double sqrt(int a) {
            return Math.sqrt(a);
        }
    }

    Formula mFormula = new Formula() {
        @Override
        public double calculate(int a) {
            return sqrt(a);
        }
    };

    /**
     * Lambda表达式，Java编译器可以自动推导出参数类型
     * 对于函数体只有一行代码的，可以去掉大括号{}以及return关键字，类似于Kotlin的单表达式函数
     */
    public void lambda() {
        List<String> names = Arrays.asList("Apple", "Android");

        //1
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });

        //2
        Collections.sort(names, (s, t1) -> {
            return s.compareTo(t1);
        });

        //3
        Collections.sort(names, (s, t1) -> s.compareTo(t1));

        /*
        4.
        @FunctionalInterface
        public interface Comparator<T> {
             int compare(T var1, T var2);
        }
        s.compareTo(t1)
        这种类似于静态方法调用的方法引用，原理是T var1中的这个T，必须是String类型，才能这样写
        特定类的任意对象的方法引用
         */
        Comparator<String> comparator = String::compareTo;  //这样更好理解一点
        Collections.sort(names, comparator);
    }

    /**
     * 函数式接口：Java是不能传递方法的，但是为了实现传递方式，采用了单方法的接口，其本质还是对象调用方法
     * <p>
     * 这一步其实也是使用Lambda表达式的特性，仅仅只包含一个抽象方法的接口，每一个该类型的lambda表达式都会被匹配到这个
     * 抽象方法。因为默认方法不算抽象方法，所以你也可以给你的函数式接口添加默认方法。
     * <p>
     * 这里使用泛型，只要满足输入是F，输出是T的，都可以接受，使用范围更广,但是本质上还是Lambda表达式
     */
    @FunctionalInterface
    interface Convert<F, T> {
        T convert(F from);
    }

    public void setConvert(Convert<String, String> convert) {
        System.out.println(convert.convert("123"));
    }

    public void functionalInterface() {
        Convert<String, Integer> convert = from -> Integer.valueOf(from);   //这一步也是Lambda表达式
        System.out.println(convert.convert("123"));
    }

    /**
     * 方法与构造函数引用，允许使用 :: 关键字来传递方法或者构造函数引用
     * 1）构造器引用
     * 2）静态方法引用
     * 3）特定类的任意对象的方法引用
     * 4）特定对象的方法引用
     * <p>
     * 对象方法引用表示必须能够赋值给一个函数式接口，来表示这个函数式接口的实现
     * <p>
     * 归根到底，就是这个函数式接口的实现
     */
    public void functionReference() {
        //通过静态方法引用表示，这只是Convert<String, Integer>接口的实现，并没有真正调用
        Convert<String, Integer> convert = Integer::valueOf;
        convert.convert("123");
    }

    public String testObjectFunctionReference(String name) {
        System.out.println(name);
        return name;
    }

    public static String testStaticFunctionReference(String name) {
        System.out.println(name);
        return name;
    }

    interface Java8Factory<T extends Java8Feature> {
        T create();
    }

    public static void main(String[] args) {
        Java8Feature java8Feature = new Java8Feature();
        System.out.println("Test:" + java8Feature.mFormula.calculate(10));

        //通过对象引用，也就是testFunctionReference这个方法，就是Convert<T,F>的实现
        Convert<String, String> testObjectFunctionReference = java8Feature::testObjectFunctionReference;
        testObjectFunctionReference.convert("Blend");

        //通过类引用，也就是testStaticFunctionReference这个方法，就是Convert<T,F>的实现
        Convert<String, String> testStaticFunctionReference = Java8Feature::testStaticFunctionReference;
        testStaticFunctionReference.convert("Blend");

        //这种方式，就是相当于传入
        java8Feature.setConvert(testObjectFunctionReference);
        java8Feature.setConvert(testStaticFunctionReference);
        java8Feature.setConvert(java8Feature::testObjectFunctionReference);

        //构造函数是使用::关键字引用
        Java8Factory<Java8Feature> java8Factory = Java8Feature::new;
        Java8Feature factory = java8Factory.create();

        //上面的写法等价于下面这样的
        Java8Factory<Java8Feature> java8Factory1 = new Java8Factory<Java8Feature>() {
            @Override
            public Java8Feature create() {
                return new Java8Feature();
            }
        };
        Java8Feature factory1 = java8Factory1.create();
    }

}
