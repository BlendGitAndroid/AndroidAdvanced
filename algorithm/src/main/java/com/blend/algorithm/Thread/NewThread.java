package com.blend.algorithm.Thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 启动线程的方式：
 * 1.X extends Thread。
 * 2.X implements Runnable。
 * 3.X implements Callable。
 * 在执行完任务之后无法获取执行结果。从Java 1.5开始，就提供了Callable和Future，通过它们可以在任务执行完毕之后得到任务执行结果。
 * <p>
 * Runnable、Callable、Future和FutureTask:
 * 1.Runnable是一个接口，在它里面只声明了一个run()方法，由于run()方法返回值为void类型，所以在执行完任务之后无法返回任何结果。
 * 2.Callable位于java.util.concurrent包下，它也是一个接口，在它里面也只声明了一个方法，只不过这个方法叫做call()，
 * 这是一个泛型接口，call()函数返回的类型就是传递进来的V类型。
 * 3.Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果。必要时可以通过get方法获取执行结果，
 * 该方法会阻塞直到任务返回结果。
 * 4.因为Future只是一个接口，所以是无法直接用来创建对象使用的，因此就有了下面的FutureTask。
 * FutureTask类实现了RunnableFuture接口，RunnableFuture继承了Runnable接口和Future接口，而FutureTask实现了RunnableFuture接口。
 * 所以它既可以作为Runnable被线程执行，又可以作为Future得到Callable的返回值。
 * 事实上，FutureTask是Future接口的一个唯一实现类。要new一个FutureTask的实例，有两种方法：传入Runnable或者Callable。
 */
class NewThread {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        UseThread useThread = new UseThread();
        useThread.start();

        UseRun useRun = new UseRun();
        new Thread(useRun).start();

        UseCall useCall = new UseCall();
        FutureTask<String> futureTask = new FutureTask<>(useCall);
        new Thread(futureTask).start();
        Thread.sleep(1000);
        System.out.println("do my work");
        if (!futureTask.isDone()) {
            futureTask.cancel(true); //其实这里的取消，也是用的interrupt方法
        }
        if (futureTask.isCancelled()) {
            System.out.println("future is cancel");
        } else {
            System.out.println(futureTask.get());//由于Callable线程休眠了5秒，所以这里会被阻塞5秒
        }
    }

    //扩展自Thread类
    private static class UseThread extends Thread {
        @Override
        public void run() {
            super.run();
            System.out.println("I am extends Thread");
        }
    }

    //实现Runnable接口
    private static class UseRun implements Runnable {

        @Override
        public void run() {
            System.out.println("I am implements Runnable");
        }
    }

    //实现Callable接口，允许有返回值
    private static class UseCall implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println("I am implements Callable");
            Thread.sleep(5000);
            return "CallResult";
        }
    }

}
