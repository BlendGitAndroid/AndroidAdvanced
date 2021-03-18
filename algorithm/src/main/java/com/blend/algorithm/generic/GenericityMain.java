package com.blend.algorithm.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * 泛型的好处：
 * 1.适用于多种数据类型执行相同的代码，就不需要重载很多方法，使用一个泛型就够了。
 * 2.泛型中的类型在使用时指定，不需要强制类型转换。比如List集合，集合不会记住此对象的类型，当再次从集合中取出此对象时，该对象
 * 的编译类型变成了Object类型，但其运行时类型任然为其本身类型。
 * <p>
 * 泛型的定义：即参数化类型。就是将类型由原来具体的类型参数化，类似于方法中的变量参数，此时类型也定义成参数形式（可以称之为类型形参），
 * 然后在使用/调用时传入具体的类型（类型实参）。泛型的本质是为了参数化类型（在不创建新的类型的情况下，通过泛型指定的不同类型来控制形
 * 参具体限制的类型）。也就是说在泛型使用过程中，操作的数据类型被指定为一个参数，这种参数类型可以用在类、接口和方法中，分别被称为泛型
 * 类、泛型接口、泛型方法。
 * 泛型类：实现泛型类的接口有两种实现方法：1.未传入泛型实参。2.传入泛型实参。
 * 泛型方法：在调用方法的时候指明泛型的具体类型 ，泛型方法可以在任何地方和任何场景中使用，包括普通类和泛型类。注意泛型类中定义的普通
 * 方法和泛型方法的区别。
 * <p>
 * 限定类型变量：限定类型变量，对类型变量加以约束。
 * <p>
 * 泛型中的约束与局限性：
 * 1.不能用基本类型实例化类型参数。
 * 2.运行时类型查询只适用于原始类型。
 * 3.泛型类的静态上下文中类型变量失效，但是要是静态的泛型方法就可以。原因是不能在静态域或方法中引用类型变量。因为泛型是要在对象创建的时
 * 候才知道是什么类型的，而对象创建的代码执行先后顺序是static的部分，然后才是构造函数等等。所以在对象初始化之前static的部分已经执行了，
 * 如果你在静态部分引用的泛型，那么毫无疑问虚拟机根本不知道是什么东西，因为这个时候类还没有初始化。
 * 4.不能实例化类型变脸，比如 new T()。
 * 5.泛型类不能extends Exception/Throws，并且不能捕获泛型类对象。
 * 6.不能创建参数化类型的数组。
 * <p>
 * 泛型类的继承规则：
 * 有一个类：public class Employee{}
 * 它的子类：public class Worker extends Employee{}
 * 请问Pair<Employee>和Pair<Worker>是继承关系吗？
 * 答案：不是，他们之间没有什么关系
 * <p>
 * 通配符类型：
 * 1.？ extends X。表示传递给方法的参数，必须是X的子类（包括X本身），Extends只能安全的get数据
 * 2.？ super X。表示传递给方法的参数，必须是X的超类（包括X本身），super只能安全的set数据
 * <p>
 * 无限定通配符：表示对类型没有什么限制，可以把？看成所有类型的父类，如Pair<?>
 * 比如：
 * ArrayList<T> al=new ArrayList<T>(); 指定集合元素只能是T类型
 * ArrayList<?> al=new ArrayList<?>();集合元素可以是任意类型，这种没有意义，一般是方法中，只是为了说明用法。
 * <p>
 * 虚拟机是如何实现泛型的？
 * Java语言中的泛型则不一样，它只在程序源码中存在，在编译后的字节码文件中，就已经替换为原来的原生类型（Raw Type，也称为裸类型）了，
 * 并且在相应的地方插入了强制转型代码，因此，对于运行期的Java语言来说，ArrayList＜int＞与ArrayList＜String＞就是同一个类，所以
 * 泛型技术实际上是Java语言的一颗语法糖，Java语言中的泛型实现方法称为类型擦除，基于这种方法实现的泛型称为伪泛型。
 * 之所以会出现这种情况，是因为JDK1.5之后才出现的泛型，为了兼容之前的版本，就使用泛型擦除。
 * E:element
 * K:key
 * V:value 这些都是约定俗成的
 */
class GenericityMain {

    public static void main(String[] args) {
        //未传入泛型实参，需要指定具体类型
        GenericInterfaceImpl1<Integer> impl1 = new GenericInterfaceImpl1<Integer>();
        impl1.setData(5);
        System.out.println(impl1.next(5));

        //但是，对于未传入泛型实参的，调用的时候就不传入，出来的结果也是5，但是如果输出的类型强制转换为String，
        // 编译器就不会报错，但是运行时会报错，这既是泛型的作用
        GenericInterfaceImpl1 impl2 = new GenericInterfaceImpl1();
        // System.out.println((String) impl2.next(5));

        //传入泛型实参，和普通类没有什么区别
        GenericInterfaceImpl impl = new GenericInterfaceImpl();
        System.out.println(impl.next("Blend"));

        // 运行时类型查询只适用于原始类型
        // if (impl1 instanceof GenericInterfaceImpl1<Integer>)  不允许
        // if (impl1 instanceof GenericInterfaceImpl1<T>)  不允许
        System.out.println(impl1.getClass());

        //不能创建参数化类型的数组
        // GenericInterfaceImpl1<Integer>[] array = new GenericInterfaceImpl1<>()[10];

    }

    //泛型类不能extends Exception/Throws
    // private class Problem<T> extends Exception{}

    //不能捕获泛型类对象
    // public <T extends Exception> void doSome(){
    //     try {
    //
    //     } catch (T e){
    //
    //     }
    // }
    //但是下面的方法是可以的
    public <T extends Throwable> void doSome(T t) throws T {
        try {

        } catch (Throwable e) {
            throw t;
        }
    }

    //通配符类型
    public static class Fruit {

    }

    public static class Apple extends Fruit {

    }

    public static class HongFuShi extends Apple {

    }

    private static void print(GenericInterfaceImpl1<Fruit> fruit) {

    }

    //表示传递给方法的参数，必须是X的子类（包括X本身），这个就是协变
    private static void printExtends(GenericInterfaceImpl1<? extends Apple> apple) {
        Apple data = apple.getData();
        //但是下面的三个方法设置参数是不允许的，因为程序不知道你setData的值有多小，只知道你传入的是Apple的子类
        //不能将一个父类变成子类类型，所以不准setData
        // apple.setData(new Fruit());
        // apple.setData(new Apple());
        // apple.setData(new HongFuShi());
    }

    //表示传递给方法的参数，必须是X的父类（包括X本身），这个就是逆变
    private static void printSuper(GenericInterfaceImpl1<? super Apple> apple) {
        Object data = apple.getData();
        // apple.setData(new Fruit());
        //而下面这个只能传入Apple及其子类，原因是外部传入的apple为是Apple的本身及其父类，
        //setData的时候，能将子类变成父类，所以setData(子类)是可以的，要是setData(父类)不行，因为不能将父类变成子类
        apple.setData(new Apple());
        apple.setData(new HongFuShi());
    }

    public static void setList(List<?> list) {
        // list.add("Blend"); 但是这个是不能添加的
        // list.add(1);
        Object o = list.get(1);

    }

    public static void use() {
        GenericInterfaceImpl1<Fruit> furit = new GenericInterfaceImpl1<Fruit>();
        GenericInterfaceImpl1<Apple> apple = new GenericInterfaceImpl1<>();
        print(furit);
        // print(apple);//这里是编译不了的，因为GenericInterfaceImpl1<Fruit> 和 GenericInterfaceImpl1<Apple>没有关系

        //为了解决上面的问题，所以加入了通配符
        //无限定通配符
        ArrayList<?> list = new ArrayList<>();//集合元素可以是任意类型，这种没有意义，一般是方法中，只是为了说明用法。

        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();
        setList(list1);
        setList(list2);


        /**
         * extends用法传值
         */
        GenericInterfaceImpl1<Fruit> fruitExtends = new GenericInterfaceImpl1<>();
        GenericInterfaceImpl1<Apple> appleExtends = new GenericInterfaceImpl1<>();
        GenericInterfaceImpl1<HongFuShi> hongfushiExtends = new GenericInterfaceImpl1<>();
        // printExtends(fruitExtends); //fruit不是子类，所以不行
        printExtends(appleExtends); //这里是可以的，因为Apple是本身
        printExtends(hongfushiExtends); //但是这里是可以的，因为hongfushi是Fruit的子类

        /**
         * extends用法使用
         */
        GenericInterfaceImpl1<? extends Apple> fruit1 = new GenericInterfaceImpl1<>();
        Apple data = fruit1.getData(); //这里的getData返回的是Apple
        //但是下面的三个方法设置参数是不允许的
        // fruit1.setData(new Fruit());
        // fruit1.setData(new Apple());
        // fruit1.setData(new HongFuShi());
        /*
        为何？
        道理很简单，？ extends X  表示类型的上界，类型参数是X的子类，那么可以肯定的说，get方法返回的一定是个X（不管是X或者X的子类）
        编译器是可以确定知道的。但是set方法只知道传入的是个X，至于具体是X的那个子类，不知道，因为子类有可能很多，所以索性就不能设置数据。
        总结：主要用于安全地访问数据，可以访问X及其子类型，并且不能写入非null的数据。
         */

        /**
         * super方法传值
         */
        GenericInterfaceImpl1<Object> objectSuper = new GenericInterfaceImpl1<>();
        GenericInterfaceImpl1<Fruit> fruitSuper = new GenericInterfaceImpl1<>();
        GenericInterfaceImpl1<Apple> appleSuper = new GenericInterfaceImpl1<>();
        GenericInterfaceImpl1<HongFuShi> hongFuShiSuper = new GenericInterfaceImpl1<>();
        printSuper(objectSuper); //因为Apple是Fruit的父类，要传入的值是本身或者其父类
        printSuper(fruitSuper); //因为Apple是Fruit的父类，要传入的值是本身或者其父类
        printSuper(appleSuper);
        // printSuper(hongFuShiSuper); //这里传入的是子类，就不行

        /**
         * super用法使用
         */
        //？ super X。表示传递给方法的参数，必须是X的超类（包括X本身）
        GenericInterfaceImpl1<? super Apple> apple1 = new GenericInterfaceImpl1<>();
        Object data1 = apple1.getData(); //这里返回的是Object
        // apple1.setData(new Fruit());
        apple1.setData(new Apple());
        apple1.setData(new HongFuShi());
        /*
        为何？
        ？ super  X  表示类型的下界，类型参数是X的超类（包括X本身），那么可以肯定的说，get方法返回的一定是个X的超类，那么到底是哪个
        超类？不知道，但是可以肯定的说，Object一定是它的超类，所以get方法返回Object。编译器是可以确定知道的。对于set方法来说，编译器
        不知道它需要的确切类型，但是X和X的子类可以安全的转型为X。
        总结：主要用于安全地写入数据，可以写入X及其子类型。
         */
    }
}
