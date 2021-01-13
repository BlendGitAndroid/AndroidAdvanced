package com.blend.algorithm.compara;

class ComparableTest implements Comparable<ComparableTest> {

    String name;

    int age;

    public ComparableTest(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "ComparableTest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public int compareTo(ComparableTest test) {     //升序
        if (this.age < test.age) {
            return -1;
        } else if (this.age > test.age) {
            return 1;
        }
        return 0;
    }
}
