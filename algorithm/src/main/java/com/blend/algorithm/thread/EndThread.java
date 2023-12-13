package com.blend.algorithm.thread;

/**
 * 线程中止，要么是run方法执行完了，要么是抛出一个未处理的异常导致线程提前结束。
 * <p>
 * 手动中止：暂停、恢复和停止操作对应在线程Thread的API就是suspend()、resume()和stop()。但是这些API是过期的，也就是不建议使用的。
 * 不建议使用的原因主要有：以suspend()方法为例，在调用后，线程不会释放已经占有的资源（比如锁），而是占有着资源进入睡眠状态，这样容易
 * 引发死锁问题。同样，stop()方法在终结一个线程时不会保证线程的资源正常释放，通常是没有给予线程完成资源释放工作的机会，因此会导致程序
 * 可能工作在不确定状态下。正因为suspend()、resume()和stop()方法带来的副作用，这些方法才被标注为不建议使用的过期方法。
 * <p>
 * 安全的中止：安全的中止则是其他线程B通过调用某个线程A的interrupt()方法对其进行中断操作, 中断好比其他线程对该线程打了个招呼，“A，你
 * 要中断了”，不代表线程A会立即停止自己的工作，同样的A线程完全可以不理会这种中断请求。因为java里的线程是协作式的，不是抢占式的。线程通
 * 过检查自身的中断标志位是否被置为true来进行响应，线程通过方法isInterrupted()来进行判断是否被中断，也可以调用静态方法Thread.interrupted()
 * 来进行判断当前线程是否被中断，不过Thread.interrupted()会同时将中断标识位改写为false。
 * <p>
 * 阻塞状态的中止：
 * 如果一个线程处于了阻塞状态（如线程调用了thread.sleep、thread.join、thread.wait、），则线程在检查中断标示时如果发现中断标示为true，
 * 则会在这些阻塞方法调用处抛出InterruptedException异常，并且在抛出异常后会立即将线程的中断标示位清除，即重新设置为false。
 * <p>
 * 不建议自定义一个取消标志位来中止线程的运行。（如：为什么说 volatile 修饰标记位停止方法是错误的）
 * 因为run方法里有阻塞调用时会无法很快检测到取消标志，线程必须从阻塞调用返回后，才会检查这个取消标志。这种情况下，使用中断会更好，因为：
 * 一、一般的阻塞方法，如sleep等本身就支持中断的检查。
 * 二、检查中断位的状态和检查取消标志位没什么区别，用中断位的状态还可以避免声明取消标志位，减少资源的消耗。
 * <p>
 * 注意：处于死锁状态的线程无法被中断
 * <p>
 * `Thread.interrupted()`和`isInterrupted()`是Java中用于检查线程中断状态的两个方法，它们之间有一些区别。
 * <p>
 * 1. `Thread.interrupted()`方法：
 * - `Thread.interrupted()`是一个静态方法，可以通过`Thread`类直接调用。
 * - 调用`Thread.interrupted()`方法会检查当前线程的中断状态，并将中断状态重置为"未中断"。
 * - 如果当前线程的中断状态为"中断"，即调用`interrupt()`方法中断了线程，那么`Thread.interrupted()`会返回true；
 * - 如果线程的中断状态为"未中断"，则返回false。
 * - 这个方法一般用于在检查线程的中断状态同时清除中断状态，一般在线程的执行逻辑中使用。
 * <p>
 * 2. `isInterrupted()`方法：
 * - `isInterrupted()`是一个实例方法，需要通过具体的`Thread`对象调用。
 * - 调用`isInterrupted()`方法只会检查当前线程的中断状态，不会修改中断状态。
 * - 如果当前线程的中断状态为"中断"，即调用`interrupt()`方法中断了线程，那么`isInterrupted()`会返回true；
 * - 如果线程的中断状态为"未中断"，则返回false。
 * - 这个方法一般用于在检查线程的中断状态时，不需要清除中断状态，一般在线程间的通信或者判断中使用。
 * <p>
 * 总之，`Thread.interrupted()`方法会检查当前线程的中断状态并清除中断状态，而`isInterrupted()`方法只会检查当前线程的中断状态而不清除。
 */
class EndThread {

    public static void main(String[] args) throws InterruptedException {
        Thread endThread = new UseThread("endThread");
        endThread.start();
        Thread.sleep(1000);
        endThread.interrupt();
    }

    private static class UseThread extends Thread {

        public UseThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();
            String name = Thread.currentThread().getName();
            System.out.println(name + " interrupt flag = " + isInterrupted());
            // while (!Thread.interrupted()){  // currentThread().isInterrupted(true)
            while (!isInterrupted()) {  //isInterrupted(false), 其实这两个中断方法都是调用的一个native方法，只不过一个返回true，一个返回false
                // while (true) {   //在这种情况下即使调用了interrupt也没有被中断
                System.out.println(name + " inner interrupt flag = " + isInterrupted());
            }
            System.out.println(name + " interrupt flag = " + isInterrupted());
        }
    }

}
