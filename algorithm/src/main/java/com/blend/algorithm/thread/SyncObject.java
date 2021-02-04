package com.blend.algorithm.thread;

/**
 * Java支持多个线程同时访问一个对象或者对象的成员变量，关键字synchronized可以修饰方法或者以同步块的形式来进行使用，它主
 * 要确保多个线程在同一个时刻，只能有一个线程处于方法或者同步块中，它保证了线程对变量访问的可见性和排他性，又称为内置锁机制。
 * <p>
 * addCount()和addCount3()锁的都是本类的对象，是一样的。
 * <p>
 * 对象锁是用于对象实例方法，或者一个对象实例上的，类锁是用于类的静态方法或者一个类的class对象上的。我们知道，类的对象实例
 * 可以有很多个，但是每个类只有一个class对象，所以不同对象实例的对象锁是互不干扰的，但是每个类只有一个类锁。
 * 但是有一点必须注意的是，其实类锁只是一个概念上的东西，并不是真实存在的，类锁其实锁的是每个类的对应的class对象。
 * 类锁和对象锁之间也是互不干扰的，因为锁的对象不同。
 */
class SyncObject {

    private long count = 0;
    private final Object obj = new Object();  //作为一个锁

    public synchronized void addCount() {
        count++;
    }

    public void addCount2() {
        synchronized (obj) {
            count++;
        }
    }

    public void addCount3() {
        synchronized (this) {
            count++;
        }
    }

    //类锁，实际是锁类的class对象，全局只有一份
    private static synchronized void addCountClass() {

    }

    private static final Object objStatic = new Object();

    private void addCountClass2() {
        synchronized (objStatic) { //类似于类锁，objStatic在全虚拟机只有一份
            count++;
        }
    }


    private static class CountThread extends Thread {

        private SyncObject obj;

        public CountThread(SyncObject obj) {
            this.obj = obj;
        }

        @Override
        public void run() {
            super.run();
            for (int i = 0; i < 1000; i++) {
                obj.addCount3();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SyncObject syncObject = new SyncObject();
        //启动两个线程
        CountThread count1 = new CountThread(syncObject);
        CountThread count2 = new CountThread(syncObject);
        count1.start();
        count2.start();
        Thread.sleep(100);
        System.out.println(syncObject.count);
    }

}
