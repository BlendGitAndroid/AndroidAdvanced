package com.blend.algorithm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 代理模式给某一个对象提供一个代理对象，并由代理对象控制对原对象的引用。
 * <p>
 * 目的：
 * 1.通过引入代理对象的方式来间接访问目标对象，防止直接访问目标对象给系统带来的不必要复杂性。
 * 2.通过代理对象对原有的业务增强。
 * <p>
 * 代理模式一般有三个角色：
 * 1.抽象角色：指代理角色和真实角色对外提供的公共方法，一般为一个接口。
 * 2.真实角色：需要实现抽象角色接口，定义了真实角色所要实现的业务逻辑，以便供代理角色调用。也就是真正的业务逻辑在此。
 * 3.代理角色：需要实现抽象角色接口，是真实角色的代理，通过真实角色的业务逻辑方法来实现抽象方法，并可以附加自己的操作。
 * 将统一的流程控制都放到代理角色中处理！而访问者不再访问真实角色，而是去访问代理角色。
 * <p>
 * 静态代理：静态代理在使用时,需要定义接口或者父类,被代理对象与代理对象一起实现相同的接口或者是继承相同父类。一般来说，
 * 被代理对象和代理对象是一对一的关系，当然一个代理对象对应多个被代理对象也是可以的。
 * 缺点：违反开闭原则
 * 静态代理，一对一则会出现时静态代理对象量多、代码量大，从而导致代码复杂，可维护性差的问题，一对多则代理对象会出现扩展能力差的问题。
 * <p>
 * 动态代理：是指在使用时再创建代理类和实例。
 * 优点：
 * 只需要1个动态代理类就可以解决创建多个静态代理的问题，避免重复、多余代码，更强的灵活性。
 * 缺点：
 * 1.效率低，相比静态代理中直接调用目标对象方法，动态代理则需要先通过Java反射机制，从而间接调用目标对象方法。
 * 2.应用场景局限，因为Java的单继承特性（每个代理类都继承了 Proxy 类），即只能针对接口创建代理类，不能针对类创建代理类。
 * <p>
 * 在java的动态代理机制中，有两个重要的类或接口，一个是InvocationHandler接口、另一个则是 Proxy类，这个类和接口是实现我们动态代理
 * 所必须用到的。InvocationHandler接口是给动态代理类实现的，负责处理被代理对象的操作的，而Proxy是用来创建动态代理类实例对象的，因为
 * 只有得到了这个对象我们才能调用那些需要代理的方法。
 * <p>
 * 类的完整生命周期：
 * java源文件 -> java字节码文件(.class文件) -> Class对象 -> 实例对象 -> 卸载
 * 那么字节码文件来源：
 * 1.从硬盘中。
 * 2.从内存中。
 * 动态代理就是在内存中动态拿到代理者的java字节码的byte数组，然后加载字节码文件，进行类之后的操作。
 * 所以动态代理就是动态的生成代理者，不管静态代理还是动态代理，都得代理一个真实的对象。
 */
class ProxyMain {

    public static void main(String[] args) {
        //静态代理
        Tools factory = new Factory();  //真实的工厂
        ToolsA factoryA = new FactoryA();  //真实一个A工厂，但是想让代理者代理A的，就比较难扩展，扩展性差
        Tools proxyer = new Proxyer(factory);   //代理者需要知道代理的是哪个工厂
        proxyer.saleTools(50);

        System.out.println("-----------------------------");

        //动态代理
        Factory factory1Real = new Factory(); //真实的工厂
        InvocationHandler dynamic = new ProxyerDynamic(factory1Real);   //将工厂交给代理者
        //这里的返回值一定是接口，并且生成的类是：$Proxy0
        Tools factoryDynamic = (Tools) Proxy.newProxyInstance(factory1Real.getClass().getClassLoader(), factory1Real.getClass().getInterfaces(), dynamic);
        String result = factoryDynamic.saleTools(50);
        System.out.println(result);

    }


}
