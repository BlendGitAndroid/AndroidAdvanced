package com.blend.algorithm.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 如果一个线程处于了阻塞状态（如线程调用了thread.sleep、thread.join、thread.wait、），则线程在检查中断标示时如果发现中断标示为true，
 * 则会在这些阻塞方法调用处抛出InterruptedException异常，并且在抛出异常后会立即将线程的中断标示位清除，即重新设置为false。
 * <p>
 * 下面这个例子就很好的解释了这种现象，为了避免程序在出现异常的时候还继续运行，在while循环中手动调用线程的intercept()方法给标志位设置为true。
 */
class InterruptedExceptionThread {

    public static void main(String[] args) throws InterruptedException {

        Thread useThread = new UseThread("HasInterrupted");
        useThread.start();
        System.out.println("Main:" + formater.format(new Date()));
        Thread.sleep(800);
        System.out.println("Main begin interrupt thread:" + formater.format(new Date()));
        useThread.interrupt();
    }

    private static SimpleDateFormat formater
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss_SSS");

    private static class UseThread extends Thread {

        public UseThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            while (!isInterrupted()) {
                try {
                    System.out.println("UseThread:" + formater.format(new Date()));
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println(threadName + " catch interrput flag is "
                            + isInterrupted() + " at "
                            + (formater.format(new Date())));
                    interrupt();
                    e.printStackTrace();
                }
            }
            System.out.println(threadName + " interrput flag is "
                    + isInterrupted());
        }
    }
}
