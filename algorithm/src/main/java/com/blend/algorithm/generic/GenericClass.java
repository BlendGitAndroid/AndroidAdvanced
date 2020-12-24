package com.blend.algorithm.generic;

/**
 * 泛型类
 */
class GenericClass<T, K> {

    // private static T t;

    public static <T> String getStatic(T t) {
        return t.toString();
    }

    public T data;
    public K name;

    public GenericClass() {
    }

    public GenericClass(T data, K name) {
        this.data = data;
        this.name = name;
    }

    //泛型方法，第一个<U>就是修饰符，表示这是一个泛型方法，第一个U是返回值
    public <U> U genericMethod(U... u) {
        return u[u.length / 2];
    }

    //注意，这个方法和上面的这个方法是没有区别的，这个方法里面的T和全局的T是不一样的，为了区分不同的泛型参数，
    //最好使用不同的大写字母表示
    public <T> T genericMethod1(T... u) {
        return u[u.length / 2];
    }

    //这也是一个泛型方法，U，E是修饰符，但是String是返回值
    public <U, E> String getUE(U u, E e) {
        return u.toString() + e.toString();
    }

    //这就是一个普通的成员方法，虽然这个方法里面也有泛型
    public T getT(T t) {
        return t;
    }

    //限定类型变量，如何确保传入的两个值一定要有compareTo方法？
    //那么解决这个问题的方案就是将T限制为实现了接口Comparable的类。
    public <T extends Comparable<T>> T min(T a, T b) {
        if (a.compareTo(b) < 0) {
            return a;
        } else {
            return b;
        }
    }

    public static void main(String[] args) {
        GenericClass<String, Integer> genericClass = new GenericClass<>("Blend", 18);
        System.out.println(genericClass.min(1, 2));
        //这一行就会报错，因为GenericClass没有实现Comparable接口
        // System.out.println(genericClass.min(new GenericClass(), new GenericClass()));
    }
}
