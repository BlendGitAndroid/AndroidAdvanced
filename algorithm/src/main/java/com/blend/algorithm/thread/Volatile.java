package com.blend.algorithm.thread;

/**
 * 下面的代码的运行结果是不能保证的，大概率有时候会运行出：
 * PrintThread is running...
 * main is end!
 * 加上volatile关键字后，就可以运行正确了
 * <p>
 * 这是什么原因呢？
 * 这是因为在并发编程时，对于同一个变量ready，在不同的线程内部会有一个ready副本，当一个线程改变ready的值时，
 * 这个值的改变对另外一个正在使用ready变量的线程是不可见的，volatile关键字就是保证了变量的可见性。
 * <p>
 * volatile变量能保证可见性，但是保证不了原子性。
 * 所以volatile关键字适合用于一个线程写数据，多个线程读数据的情况。
 * 首先线程A读取了i的变量的值，这个时候线程切换到了B，线程B同样从主内存中读取i的值，由于线程A没有对i做过任何修改，此时线
 * 程B获取到的i仍然是100。线程B工作内存中为i执行了加1的操作，但是没有刷新到主内存中，这个时候又切换到了A线程，A线程直接对
 * 工作内存中的100进行加1运输（因为A线程已经读取过i的值了），由于线程B并未写入i的最新值，这个时候A线程的工内存中的100不会
 * 失效。 最后，线程A将i=101写入主内存中，线程B也将i=101写入主内存中。 始终需要记住，i++ 的操作是3步骤！这样理解起来就更
 * 容易了。
 */
class Volatile {

    private volatile static boolean ready = false;
    private static int number = 0;

    public static void main(String[] args) throws InterruptedException {
        // 保证可见性
        new PrintThread().start();
        Thread.sleep(5);
        number = 50;
        ready = true;
        Thread.sleep(5);
        System.out.println("main is end!");

        //不能保证原子性
        Volatile test = new Volatile();
        CountThread countThread1 = new CountThread(test);
        CountThread countThread2 = new CountThread(test);
        countThread1.start();
        countThread2.start();
        Thread.sleep(50);
        System.out.println(number);
    }

    public void addNumber() {
        number++;
    }

    private static class PrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            System.out.println("PrintThread is running...");
            while (!ready) ;
            System.out.println("number = " + number);
        }
    }

    private static class CountThread extends Thread {

        private Volatile opera;

        public CountThread(Volatile opera) {
            this.opera = opera;
        }

        @Override
        public void run() {
            super.run();
            for (int i = 0; i < 1000; i++) {
                opera.addNumber();
            }
        }
    }


}
