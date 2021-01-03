package com.blend.architecture.dagger2.zhuruyilai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.architecture.R;

/**
 * 依赖：依赖是类与类之间的连接，依赖关系表示一个类依赖于另外一个类，通俗来讲，就是一种需要，列如一个人(Person)可以买车(Car)
 * 和房子(House)，Person类依赖于Car类和House类。
 * 依赖倒置原则的定义如下：
 * 1.上层模块不应该依赖底层模块，它们都应该依赖于抽象。
 * 2.抽象不应该依赖于细节，细节应该依赖于抽象。
 */
public class ZhuruyilaiMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuruyilai_main);
        testPerson();
        testPerson2();
        testPerson3();
        testPerson4();
    }

    /*
    创建了一个 Person 类，它拥有一台自行车，出门的时候就骑自行车。
    不过，自行车适应很短的距离。如果，我要出门逛街呢？自行车就不大合适了。于是就要改成汽车。
    不过，如果我要到北京去，那么汽车也不合适了。
    有没有一种方法能让 Person 的变动少一点呢？因为这是最基础的演示代码，如果工程大了，代码复杂了，Person面对需求变动时改动的地方会更多。
    在这个列子找中Person就是依赖于Car，Bike等交通工具。
     */
    private void testPerson() {
        Person person = new Person();
        person.goOut();
    }

    /*
     根据依赖倒置原则来对代码进行改造。
     上层模块不应该依赖底层模块，它们都应该依赖于抽象。
     抽象不应该依赖于细节，细节应该依赖于抽象。
     首先是上层模块和底层模块的拆分。
     按照决策能力高低或者重要性划分，Person 属于上层模块，Bike、Car 和 Train 属于底层模块。
     可以看到，依赖倒置，实际上是面向接口编程。
     */
    private void testPerson2() {
        Person2 person2 = new Person2();
        person2.goOut();
    }


    /*
    控制反转，IOC，Inversion of Control的缩写，意思是对于控制权的反转，那么什么是控制权呢？
    Person自己掌握着内部mDrivable的实例化，现在，我们可以更改一种方式。将 mDrivable 的实例化移到 Person 外面。
    就这样无论出行方式怎么变化，Person 这个类都不需要更改代码了。
    在上面代码中，Person 把内部依赖的创建权力移交给了 Person2这个类中的 main() 方法。也就是说 Person 只关心依赖提供
    的功能，但并不关心依赖的创建。
    这种思想其实就是 IoC，IoC 是一种新的设计模式，它对上层模块与底层模块进行了更进一步的解耦。控制反转的意思是反转了上层
    模块对于底层模块的依赖控制。
     */
    private void testPerson3() {
        Person2 person3 = new Person2(new Bike());
        person3.goOut();
    }

    /*
    依赖注入，上面test3中的构造区注入是IOC的一种手段，什么意思呢？
    为了不因为依赖实现的变动而去修改 Person，也就是说以可能在Drivable实现类的改变下不改动Person这个类的代码，尽可能减少
    两者之间的耦合。我们需要采用上一节介绍的IoC模式来进行改写代码。
    这个需要我们移交出对于依赖实例化的控制权，那么依赖怎么办？Person 无法实例化依赖了，它就需要在外部（IoC 容器）赋值给它，
    这个赋值的动作有个专门的术语叫做注入（injection），需要注意的是在 IoC 概念中，这个注入依赖的地方被称为 IoC 容器，
    但在依赖注入概念中，一般被称为注射器 （injector)。
    表达通俗一点就是：我不想自己实例化依赖，你（injector）创建它们，然后在合适的时候注入给我
    实现依赖注入的三种方式：
    1. 构造函数中注入
    2. setter 方式注入
    3. 接口注入
     */
    private void testPerson4() {
        Person2 person4 = new Person2(new Bike());  //构造器注入
        person4.setDrivable(new Bike());    //setter方式注入
        person4.set(new Bike());    //接口注入，表示一种依赖配置的能力

    }
}