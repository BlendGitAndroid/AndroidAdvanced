package com.blend.algorithm.thread.lock;

import java.util.Random;

/**
 * Lock和synchronized的比较：
 * 我们一般的Java程序是靠synchronized关键字实现锁功能的，使用synchronized关键字将会隐式地获取锁，但是它将锁的获取和
 * 释放固化了，也就是先获取再释放。synchronized属于Java语言层面的锁，也被称之为内置锁。
 * synchronized这种机制，一旦开始获取锁，是不能中断的，也不提供尝试获取锁的机制。
 * 而Lock是由Java在语法层面提供的，锁的获取和释放需要我们明显的去获取，因此被称为显式锁。
 * 并且提供了synchronized不提供的机制：
 * 1)尝试非阻塞的获取锁。当前线程尝试获取锁，如果这一段时刻锁没有被其他线程获取到，则成功获取并持有锁。
 * 2)能被中断的获取锁。与synchronized不同，获取到锁的线程能够响应中断，当获取到锁的线程被中断时，中断异常将会抛出，同时锁会被释放。
 * 3)超时获取锁。在指定的截止时间之前获取锁。如果截止时间到了扔无法获取锁，则返回。
 * <p>
 * 可重入锁：ReentrantLock:synchronized关键字隐式的支持重进入，比如一个synchronized修饰的递归方法，在方法执行时，执行线程在获取
 * 了锁之后仍能连续多次地获得该锁。ReentrantLock在调用lock()方法时，已经获取到锁的线程，能够再次调用lock()方法获取锁而不被阻塞。
 * <p>
 * 公平锁和非公平锁：如果在时间上，先对锁进行获取的请求一定先被满足，那么这个锁是公平的，反之，是不公平的。公平的获取锁，也就是等待时间
 * 最长的线程最优先获取锁，也可以说锁获取是顺序的。ReentrantLock提供了一个构造函数，能够控制锁是否是公平的。事实上，公平的锁机制往往
 * 没有非公平的效率高。原因是，在恢复一个被挂起的线程与该线程真正开始运行之间存在着严重的延迟。假设线程A持有一个锁,并且线程B请求这个锁。
 * 由于这个锁已被线程A持有,因此B将被挂起。当A释放锁时,B将被唤醒,因此会再次尝试获取锁。与此同时,如果C也请求这个锁,那么C很可能会在B被完
 * 全唤醒之前获得、使用以及释放这个锁。这样的情况是一种“双赢”的局面:B获得锁的时刻并没有推迟,C更早地获得了锁,并且吞吐量也获得了提高。
 * 就是在线程B进行上下文切换的时候，线程C已经做完了任务，等任务做完，线程B正好获取到了锁。
 * <p>
 * 读写锁：之前提到锁（synchronized和ReentrantLock）基本都是排他锁，这些锁在同一时刻只允许一个线程进行访问，而读写锁在同一时刻可以
 * 允许多个读线程访问，但是在写线程访问时，所有的读线程和其他写线程均被阻塞。读写锁维护了一对锁，一个读锁和一个写锁，通过分离读锁和写锁，
 * 使得并发性相比一般的排他锁有了很大提升。除了保证写操作对读操作的可见性以及并发性的提升之外，读写锁能够简化读写交互场景的编程方式。
 * 假设在程序中定义一个共享的用作缓存数据结构，它大部分时间提供读服务（例如查询和搜索），而写操作占有的时间很少，但是写操作完成之后的更新
 * 需要对后续的读服务可见。
 * 一般情况下，读写锁的性能都会比排它锁好，因为大多数场景读是多于写的。在读多于写的情况下，读写锁能够提供比排它锁更好的并发性和吞吐量。
 * 这也是读写分离的思想。
 * <p>
 * 下面的这个列子中，分别使用synchronized排它锁和ReadWriteLock读写锁，分别对3个写线程和30个读线程进行操作，结果差异很大：
 * 排它锁耗时：17s左右
 * 读写锁：0.7s左右
 */
class LockTest {

    static final int readWriteRatio = 10;//读写线程的比例
    static final int minthreadCount = 3;//最少线程数

    //读操作
    private static class GetRunnable implements Runnable {

        private GoodService goodsService;

        public GetRunnable(GoodService goodsService) {
            this.goodsService = goodsService;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {//操作100次
                goodsService.getNum();
            }
            System.out.println(Thread.currentThread().getName() + "read goods time:"
                    + (System.currentTimeMillis() - start) + "ms");

        }
    }

    //写操做
    private static class SetRunnable implements Runnable {

        private GoodService goodsService;

        public SetRunnable(GoodService goodsService) {
            this.goodsService = goodsService;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Random r = new Random();
            for (int i = 0; i < 10; i++) {//操作10次
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                goodsService.setNum(r.nextInt(10));
            }
            System.out.println(Thread.currentThread().getName()
                    + "write goods time:" + (System.currentTimeMillis() - start) + "ms---------");

        }
    }


    public static void main(String[] args) {
        GoodsInfo goodsInfo = new GoodsInfo("Cup", 100000, 10000);
        GoodService goodsService = new Synchronized(goodsInfo);
        for (int i = 0; i < minthreadCount; i++) {
            Thread setT = new Thread(new SetRunnable(goodsService));
            for (int j = 0; j < readWriteRatio; j++) {  //1个写线程，对应10个读线程
                Thread getT = new Thread(new GetRunnable(goodsService));
                getT.start();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setT.start();   //读线程运行100ms后，写线程开始运行
        }
    }


}
