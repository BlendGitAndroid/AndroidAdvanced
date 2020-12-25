package com.blend.algorithm.gc;

class GcRoots {
    Object o = new Object();
    static Object GCRoot1 = new Object(); //GC Roots 类静态属性对象
    final static Object GCRoot2 = new Object(); //常量的对象

    //
    public static void main(String[] args) {
        //可达
        Object object1 = GCRoot1; //=不是赋值，在对象中是引用，传递的是右边对象的地址
        Object object2 = object1;
        Object object3 = object1;
        Object object4 = object3;
    }

    public void king() {
        //不可达（方法运行完后可回收）
        Object object5 = o;//o不是GCRoots
        Object object6 = object5;
        Object object7 = object5;
    }

    //本地变量表中引用的对象
    public void stack() {
        Object ostack = new Object();    //本地变量表的对象，虚拟机栈中本地变量表中的对象
        Object object9 = ostack;
        //以上object9 在方法没有(运行完)出栈前都是可达的
    }
}
