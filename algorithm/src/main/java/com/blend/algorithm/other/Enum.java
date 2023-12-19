package com.blend.algorithm.other;

import java.util.Arrays;

public enum Enum {

    /**
     * 枚举类型是Java 5中新增特性的一部分，它是一种特殊的数据类型，之所以特殊是因为它既是一种类(class)类型却又比类类型多了
     * 些特殊的约束，但是这些约束的存在也造就了枚举类型的简洁性、安全性以及便捷性，枚举类型必须是有限的。
     * <p>
     * 实现原理：
     * 在使用关键字enum创建枚举类型并编译后，编译器会为我们生成一个相关的类，这个类继承了Java API中的java.lang.Enum类，
     * 也就是说通过关键字enum创建枚举类型在编译后事实上也是一个类类型而且该类继承自java.lang.Enum类。
     *
     * 经过对枚举类.class反编译后得到一个java文件，这个java文件是final修饰的，并且有一个私有的构造函数，枚举中定义的变量
     * 全部转化为public static final，并通过static代码块初始化。父类Enum中的定义的方法只有toString方法没有使用final
     * 修饰，因此只能覆盖toString方法
     *
     */

    /**
     * 反编译Day .class
     *
     * <p>
     * final class Day extends Enum {
     * //编译器为我们添加的静态的values()方法
     * public static Day[] values ()
     * {
     * return (Day[]) $VALUES.clone();
     * }
     * //编译器为我们添加的静态的valueOf()方法，注意间接调用了Enum也类的valueOf方法
     * public static Day valueOf (String s)
     * {
     * return (Day) Enum.valueOf(com/zejian/enumdemo/Day, s);
     * }
     * //私有构造函数
     * private Day(String s, int i)
     * {
     * super(s, i);
     * }
     * //前面定义的7种枚举实例
     * public static final Day MONDAY;
     * public static final Day TUESDAY;
     * public static final Day WEDNESDAY;
     * public static final Day THURSDAY;
     * public static final Day FRIDAY;
     * public static final Day SATURDAY;
     * public static final Day SUNDAY;
     * private static final Day $VALUES[];
     * <p>
     * static
     * {
     * //实例化枚举实例
     * MONDAY = new Day("MONDAY", 0);
     * TUESDAY = new Day("TUESDAY", 1);
     * WEDNESDAY = new Day("WEDNESDAY", 2);
     * THURSDAY = new Day("THURSDAY", 3);
     * FRIDAY = new Day("FRIDAY", 4);
     * SATURDAY = new Day("SATURDAY", 5);
     * SUNDAY = new Day("SUNDAY", 6);
     * $VALUES = (new Day[]{
     * MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
     * });
     * }
     * }
     */

    /**
     * 枚举与单例模式
     * 常见单例模式的缺点：
     * 1.序列化可能会破坏单例模式，比较每次反序列化一个序列化的对象实例时都会创建一个新的实例。
     * 2.使用反射强行调用私有构造器。解决方式可以修改构造器，让它在创建第二个实例的时候抛异常。
     * 枚举单例：
     * 1.枚举序列化是由jvm保证的，每一个枚举类型和定义的枚举变量在JVM中都是唯一的，在枚举类型的序列化和反序列化上，Java做了特殊的规定：
     * 在序列化时Java仅仅是将枚举对象的name属性输出到结果中，反序列化的时候则是通过java.lang.Enum的valueOf方法来根据名字查找枚举对象。
     * 同时，编译器是不允许任何对这种序列化机制的定制的并禁用了writeObject、readObject、readObjectNoData、writeReplace和
     * readResolve等方法，从而保证了枚举实例的唯一性。
     * 2.无法使用反射创建枚举实例，也就是说明了创建枚举实例只有编译器能够做到而已。
     * if ((clazz.getModifiers() & Modifier.ENUM) != 0)
     * throw new IllegalArgumentException("Cannot reflectively create enum objects");
     */

    MONDAY("11111"),
    TUESDAY("22222"),
    WEDNESDAY("33333"),
    THURSDAY("44444"),
    FRIDAY("55555"),
    SATURDAY("66666"),
    SUNDAY("77777");

    public String desc;//中文描述

    private Enum(String desc) {
        this.desc = desc;
    }

}

class EnumDemo {

    public static void main(String[] args) {
        // values和valueOf方法是编译器为我们添加的
        System.out.println(Arrays.toString(Enum.values())); //values()方法的作用就是获取枚举类中的所有变量，并作为数组返回
        System.out.println(Enum.valueOf("THURSDAY")); //valueOf(String name)方法与Enum类中的valueOf方法的作用类似根据名称获取枚举变量，只不过编译器生成的valueOf方法更简洁些只需传递一个参数
        System.out.println(Enum.valueOf("MONDAY").ordinal()); //返回枚举常量的序数（它在枚举声明中的位置，其中初始常量序数为零）

        System.out.println(Enum.MONDAY.name()); //返回此枚举常量的名称，在其枚举声明中对其进行声明
        System.out.println(Enum.MONDAY.desc);
    }
}
