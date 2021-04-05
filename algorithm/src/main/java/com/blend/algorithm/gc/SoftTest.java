package com.blend.algorithm.gc;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class SoftTest {
    private static class User {
        public int id = 0;
        public String name = "";

        public User(int id, String name) {
            super();
            this.id = id;
            this.name = name;
        }

        public User() {

        }
        //
        // @Override
        // public String toString() {
        //     return "User [id=" + id + ", name=" + name + "]";
        // }

    }

    private static int CACHE_INITIAL_CAPACITY = 1000 * 1024;
    private static Set<SoftReference<User>> cache = new HashSet<>();
    private static ReferenceQueue<User> referenceQueue = new ReferenceQueue();
    private static ReferenceQueue<User> referenceStringQueue = new ReferenceQueue();

    //弱引用本身不会被清理，这个记住好了，key是弱引用，gc后会被回收，但是这个KeyReference不会
    //并且引用队列中也是KeyReference，通过这个引用也能访问实体
    //会将这个弱引用本身加入到引用队列
    public static void main(String[] args) throws InterruptedException {
        // testSoftReference();
        // testSoft();
        // testString2();

        // User user = new User();
        // KeyReference keyReference = new KeyReference(user, "user2", referenceStringQueue);
        // System.out.println(keyReference);
        // user = null;
        // System.gc();
        // System.out.println(keyReference.get()); //user2
        // System.out.println(((KeyReference) referenceStringQueue.poll()).value);  //user2
        // System.out.println(keyReference.value);

        User user = new User();
        WeakReference<User> s = new WeakReference<>(user, referenceStringQueue);
        System.out.println(s.get());
        System.out.println(s);
        user = null;
        System.gc();
        System.out.println(s.get());
        System.out.println(referenceStringQueue.remove());
    }

    private static class KeyReference extends WeakReference<User> {

        String value;

        public KeyReference(User key, String value, ReferenceQueue<User> queue) {
            super(key, queue);
            this.value = value;
        }

    }

    private static void testString2() {
        String all = "hello" + "world" + "!";
        String a = "hello";
        String b = "world";
        String c = "!";
        String tmp = a + b + c;
        //为false，是因为tmp涉及到了引用，不能进行编译器优化，a + b + c最终会变成new StringBuilder进行append拼接
        System.out.println(all == tmp);
        //基本数据类型比的是值，虽然他们属于不同的实例
        System.out.println(new User().id == new User().id);
    }

    private static void testSoftReference() {
        //软引用本身被cache引用了
        for (int i = 0; i < CACHE_INITIAL_CAPACITY; i++) {
            User u = new User(0, "blend");
            cache.add(new SoftReference<>(u, referenceQueue));
            if (referenceQueue.poll() != null) {

            }
            System.out.println("size :" + cache.size());
        }
        System.out.println("END!");
    }

    private static void testSoft() {
        User u = new User(1, "King"); //new是强引用
        //被软引用关联的对象，会被垃圾回收器回收掉，但是软引用本身也是一个对象，这些不会被垃圾回收器回收掉
        SoftReference<User> userSoft = new SoftReference<User>(u);
        u = null;//干掉强引用，确保这个实例只有userSoft的软引用
        System.out.println(userSoft.get());
        System.gc();//进行一次GC垃圾回收
        System.out.println("After gc");
        System.out.println(userSoft.get());
        //往堆中填充数据，导致OOM
        List<byte[]> list = new LinkedList<>();
        try {
            for (long i = 0; i < 1000000; i++) {
                System.out.println("*************" + userSoft.get());
                list.add(new byte[1024 * 1024 * 10]); //1M的对象
            }
        } catch (Exception e) {
            //抛出了OOM异常时打印软引用对象
            System.out.println("Exception*************" + userSoft.get());
        } finally {
            System.out.println("Exception*************" + userSoft.get());
        }
        System.out.println("END");
    }
}
