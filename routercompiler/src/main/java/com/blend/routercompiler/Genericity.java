package com.blend.routercompiler;

public class Genericity {

    /**
     * Java泛型（generics）是JDK 5中引入的一个新特性，泛型提供了编译时类型安全监测机制，该机制允许程序员在编译时监测非法的类型。
     * 使用泛型机制编写的程序代码要比那些杂乱地使用Object变量，然后再进行强制类型转换的代码具有更好的安全性和可读性。泛型对于集合
     * 类尤其有用，例如，ArrayList就是一个无处不在的集合类。
     *
     * 在Java SE 1.5之前，没有泛型的情况的下，通过对类型Object的引用来实现参数的“任意化”，“任意化”带来的缺点是要做显式的强制类
     * 型转换，而这种转换是要求开发者对实际参数类型可以预知的情况下进行的。对于强制类型转换错误的情况，编译器可能不提示错误，在运行
     * 的时候才出现异常，这是一个安全隐患。泛型的好处是在编译的时候检查类型安全，并且所有的强制转换都是自动和隐式的，以提高代码的重用率。
     *
     * 泛型的本质是参数化类型，也就是所操作的数据类型被指定为一个参数。就是将类型由原来的具体的类型参数化，类似于方法中的变量参数，
     * 此时类型也定义成参数形式（可以称之为类型形参），然后在使用/调用时传入具体的类型（类型实参）。
     *
     */

    /**
     * 泛型类
     * <p>
     * Java库中 E表示集合的元素类型，K 和 V分别表示表的关键字与值的类型
     * T（需要时还可以用临近的字母 U 和 S）表示“任意类型”
     */
    public static class Pair<T, U> {

        private T name;
        private U price;

        public Pair(T name, U price) {
            this.name = name;
            this.price = price;
        }

        /*
         * 说明：
         * 我想说的其实是这个，虽然在方法中使用了泛型，但是这并不是一个泛型方法。
         * 这只是类中一个普通的成员方法，只不过他的返回值是在声明泛型类已经声明过的泛型。
         * 所以在这个方法中才可以继续使用 T 这个泛型。
         */
        public T getName() {
            return name;
        }

        public void setName(T name) {
            this.name = name;
        }

        public U getPrice() {
            return price;
        }

        public void setPrice(U price) {
            this.price = price;
        }

        /*
        静态方法无法访问类上定义的泛型；如果静态方法操作的引用数据类型不确定的时候，必须要将泛型定义在方法上。
        即：如果静态方法要使用泛型的话，必须将静态方法也定义成泛型方法 。就想下面这样，这个T并不是类申明的T
         */
        public static <T> void setTest(T t) {

        }
    }

    /**
     * 泛型接口
     */
    public interface Generator<T> {
        public T next();
    }

    public class FruitGenerator implements Generator<String> {

        @Override
        public String next() {
            return "Fruit";
        }
    }

    public class AnimalGenerator implements Generator<Integer> {

        @Override
        public Integer next() {
            return 123;
        }
    }

    public static class FruitClass<T> implements Generator<T> {

        private T t;

        @Override
        public T next() {
            return t;
        }
    }

    /**
     * 泛型通配符，在使用泛型的时候，我们还可以为传入的泛型类型实参进行上下边界的限制，如：类型实参只准传入某种类型的父类或某种类型的子类。
     * 1）上边界通配符，为泛型添加上边界，即传入的类型实参必须是指定类型的子类型，extend。
     * 2）下边界通配符，为泛型添加下边界，即传入的类型实参必须是指定类型的父类型，super。
     * 3)无边界通配符，表明在使用泛型。
     */
    public static void showKeyValue(FruitClass<Number> obj) {
        Number next = obj.next();
    }

    public static void showKeyValueWild(FruitClass<?> obj) {
        Object next = obj.next(); //可以看到，得到的是Object类型
    }


    /**
     * 泛型方法
     * 说明：
     * 1）public 与 返回值中间<T>非常重要，可以理解为声明此方法为泛型方法。
     * 2）只有声明了<T>的方法才是泛型方法，泛型类中的使用了泛型的成员方法并不是泛型方法。
     * 3）<T>表明该方法将使用泛型类型T，此时才可以在方法中使用泛型类型T。
     * 4）与泛型类的定义一样，此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型。
     */
    public static <T> T getMiddle(T... a) {  //可变长参数类型
        return a[a.length / 2];
    }

    public static void setBoundUp(FruitClass<? extends Number> fruitClass) {

    }

    public static void setBoundDown(FruitClass<? super Number> fruitClass) {

    }

    /**
     * 为什么要用通配符和边界？
     * 使用泛型的过程中，经常出现一种很别扭的情况。比如例子，我们有Fruit类，和它的派生类Apple类。
     * <p>
     * class Fruit {}
     * class Apple extends Fruit {}
     * 然后有一个最简单的容器：Plate类。盘子里可以放一个泛型的“东西”。我们可以对这个东西做最简单的“放”和“取”的动作：set( )和get( )方法。
     * <p>
     * class Plate<T>{
     * private T item;
     * public Plate(T t){item=t;}
     * public void set(T t){item=t;}
     * public T get(){return item;}
     * }
     * 现在我定义一个“水果盘子”，逻辑上水果盘子当然可以装苹果。
     * <p>
     * Plate<Fruit> p=new Plate<Apple>(new Apple());
     * 但实际上Java编译器不允许这个操作。会报错，“装苹果的盘子”无法转换成“装水果的盘子”。
     * <p>
     * error: incompatible types: Plate<Apple> cannot be converted to Plate<Fruit>
     * 所以我的尴尬症就犯了。实际上，编译器脑袋里认定的逻辑是这样的：
     * <p>
     * 苹果 IS-A 水果
     * 装苹果的盘子 NOT-IS-A 装水果的盘子
     * 所以，就算容器里装的东西之间有继承关系，但容器之间是没有继承关系的。所以我们不可以把Plate的引用传递给Plate。
     * <p>
     * 为了让泛型用起来更舒服，Sun的大脑袋们就想出了<? extends T>和<? super T>的办法，来让”水果盘子“和”苹果盘子“之间发生关系。
     * <p>
     * 泛型通配符的副作用？
     * 上界<? extends T>不能往里存，只能往外取。原因是编译器只知道容器内是Fruit或者它的派生类，但具体是什么类型不知道。可能是Fruit？
     * 可能是Apple？也可能是Banana，RedApple，GreenApple？编译器在看到后面用Plate赋值以后，盘子里没有被标上有“苹果”。
     * 而是标上一个占位符，来表示捕获一个Fruit或Fruit的子类，具体是什么类不知道。然后无论是想往里插入Apple或者Meat或者Fruit编译器都
     * 不知道能不能和匹配，所以就都不允许。但是取的时候，就知道这是一个Fruit，所以只能取。
     * 下界<? super T>能往里存，但往外取只能放在Object对象里。因为下界规定了元素的最小粒度的下限，实际上是放松了容器元素的类型控制。
     * 既然元素是Fruit的基类，那往里存粒度比Fruit小的都可以。但往外读取元素就费劲了，只有所有类的基类Object对象才能装下。但这样的话，
     * 元素的类型信息就全部丢失。
     * <p>
     * 最后看一下什么是PECS（Producer Extends Consumer Super）原则，已经很好理解了：
     * 频繁往外读取内容的，适合用上界Extends。
     * 经常往里插入的，适合用下界Super。
     */

    /**
     * Object和T不同点在于，Object是一个实打实的类,并没有泛指谁，而T可以泛指Object，比方public void printList(List<T> list){}
     * 方法中可以传入List<Object> list类型参数，也可以传入List<String> list类型参数，但是public void printList(List<Object> list){}
     * 就只可以传入List<Object> list类型参数，因为Object类型并没有泛指谁，是一个确定的类型
     * <p>
     * ?和T区别是？是一个不确定类，？和T都表示不确定的类型 ，但如果是T的话，函数里面可以对T进行操作，比方 T car = getCar()，
     * 而不能用？ car = getCar()。
     */


    public static void main(String[] args) {
        //泛型类的使用
        Pair<String, String> pairStr = new Pair<String, String>("blend", "123");
        String priceStr = pairStr.getPrice();

        Pair<String, Integer> pairInt = new Pair<String, Integer>("blend", 023);
        Integer priceInteger = pairInt.getPrice();

        //泛型方法
        getMiddle(1, 2, 3, 4, 5);
        getMiddle("1", 2, 4, 3, 5, "blend");

        //通配符
        FruitClass<Integer> integerFruitClass = new FruitClass<>();
        FruitClass<Number> numberFruitClass = new FruitClass<>();
        showKeyValue(numberFruitClass);

        //这里传Number的子类Integer是不行的，因为同一种泛型可以对应多个版本（因为参数类型是不确定的），不同版本的泛型类实例是不兼容的。
        /*
        回到上面的例子，如何解决上面的问题？总不能为了定义一个新的方法来处理Generic<Integer>类型的类，这显然与java中的多态理念相违背。
        因此我们需要一个在逻辑上可以表示同时是Generic<Integer>和Generic<Number>父类的引用类型。由此类型通配符应运而生，类型通配符一
        般是使用？代替具体的类型实参。此处’？’是类型实参，而不是类型形参。

        可以解决当具体类型不确定的时候，这个通配符就是 ?  ；当操作类型时，不需要使用类型的具体功能时，只使用Object类中的功能。
        那么可以用 ? 通配符来表未知类型。
         */
        //showKeyValue(integerFruitClass);

        //下面这种形式就是可以的
        showKeyValueWild(numberFruitClass);
        showKeyValueWild(integerFruitClass);


    }

}
