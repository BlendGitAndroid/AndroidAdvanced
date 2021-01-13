package com.blend.algorithm.compara;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Comparable和Comparator的区别就是前者要改变代码，而后者不会改变代码
 * 但是都需要Arrays.sort这个排序，默认是使用快排的。
 * 不管是那个接口，都是索引较大的在前面。当索引大的减去索引小的是正数，则升序。
 * 在调用 compare(Object o1, Object o2) 方法时，会把两个元素中索引较小的元素赋值给o2，索引较大的元素赋值给o1。
 * Comparable接口中compareTo(Object o) 方法的调用者是两个元素中索o引较大的元素，参数对象 o 则是索引较小的元素。
 * 如果要按照升序排序，则 o1小于o2返回负整数，o1与o2相等返回0，o1大于o2返回正整数
 * 如果要按照降序排序，则 o1小于o2返回正整数，o1与o2相等返回0，o1大于o2返回负整数
 */
class ComparableMain {

    public static void main(String[] args) {

        ComparableTest[] tests = new ComparableTest[5];
        tests[0] = new ComparableTest("1", 1);
        tests[1] = new ComparableTest("2", 2);
        tests[2] = new ComparableTest("3", 3);
        tests[3] = new ComparableTest("4", 4);
        tests[4] = new ComparableTest("5", 5);

        Arrays.sort(tests);

        for (ComparableTest test : tests) {
            System.out.println(test);
        }

        System.out.println("--------");
        Arrays.sort(tests, new Comparator<ComparableTest>() {       //降序
            @Override
            public int compare(ComparableTest test, ComparableTest t1) {
                if (test.age < t1.age) {
                    return 1;
                } else if (test.age > t1.age) {
                    return -1;
                }
                return 0;
            }
        });

        for (ComparableTest test : tests) {
            System.out.println(test);
        }
    }


}
