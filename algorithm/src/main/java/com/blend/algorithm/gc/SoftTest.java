package com.blend.algorithm.gc;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;

class SoftTest {
    private static class User {
        public int id = 0;
        public String name = "";

        public User(int id, String name) {
            super();
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User [id=" + id + ", name=" + name + "]";
        }

    }

    //
    public static void main(String[] args) {
        User u = new User(1, "King"); //new是强引用
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
