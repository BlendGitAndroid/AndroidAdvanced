package com.blend.algorithm.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁和非公平锁，这里使用的同一把锁的两个加锁代码来演示公平锁和非公平锁。
 * 对于公平锁而言，线程1获取到锁后，之后的线程都会被加入到阻塞队列中，当线程1执行完第一段加锁代码后，由于是公平的锁，所以会从
 * 阻塞队列中拿出一个线程执行第一段加锁代码，而线程1要执行的第二段代码则要放入阻塞队列中。
 * 但是对于公平锁而言，线程1执行完第一段加锁代码后，紧接着要执行第二段加锁代码，而其他的线程需要一个唤醒的过程，那么这个时候
 * 线程虽然没有在阻塞队列中，但是还是会执行第二段加锁代码。
 * 其原理就是公平锁会先从阻塞队列里面去拿，如果有线程则进行执行，但是非公平锁则先看现在有没有线程来请求锁，如果有则不用从阻塞队
 * 列里面去拿，而是直接执行请求线程的代码。
 * ReentrantLock默认是非公平锁,可以设置
 * Synchronized默认是非公平锁,且不能设置
 */
class FairAndUnfair {

    public static void main(String args[]) {
        PrintQueue printQueue = new PrintQueue();

        Thread thread[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        for (int i = 0; i < 10; i++) {
            thread[i].start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


class Job implements Runnable {

    private PrintQueue printQueue;

    public Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    @Override
    public void run() {
        System.out.printf("%s: Start to print a job\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("%s: End has been printed\n", Thread.currentThread().getName());
    }

}


class PrintQueue {

    private final Lock queueLock = new ReentrantLock(false);

    public void printJob(Object document) {
        queueLock.lock();
        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: during1: Printing a Job during %d seconds\n",
                    Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }

        queueLock.lock();
        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: during2: Printing a Job during %d seconds\n",
                    Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }

}
