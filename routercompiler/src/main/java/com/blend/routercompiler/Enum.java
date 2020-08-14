package com.blend.routercompiler;

public enum Enum {

    /**
     * 枚举类型是Java 5中新增特性的一部分，它是一种特殊的数据类型，之所以特殊是因为它既是一种类(class)类型却又比类类型多了
     * 些特殊的约束，但是这些约束的存在也造就了枚举类型的简洁性、安全性以及便捷性。
     * <p>
     * 实现原理：
     * 在使用关键字enum创建枚举类型并编译后，编译器会为我们生成一个相关的类，这个类继承了Java API中的java.lang.Enum类，
     * 也就是说通过关键字enum创建枚举类型在编译后事实上也是一个类类型而且该类继承自java.lang.Enum类。
     */

    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY

}

class EnumDemo {
    Enum day = Enum.FRIDAY;

}
