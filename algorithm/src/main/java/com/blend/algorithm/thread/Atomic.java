package com.blend.algorithm.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 什么是原子性操作？
 * 假定有两个操作A和B，如果从执行A的线程来看，当另一个线程执行B时，要么将B全部执行完，要么完全不执行B，那么A和B对彼此来说是原子的。
 * <p>
 * 如何实现原子操作：
 * 实现原子操作可以使用锁机制，满足基本的需求是没有问题的，但是有时候我们的需求并非这么简单，我们需要更有效，更加灵活的机制，synchronized
 * 关键字是基于阻塞的锁机制，也就是说当一个线程拥有锁的时候，访问同一资源的其它线程需要等待，直到该线程释放锁，这里会有些问题：首先，如果被
 * 阻塞的线程优先级很高很重要怎么办？其次，如果获得锁的线程一直不释放锁怎么办？（这种情况是非常糟糕的）。还有一种情况，如果有大量的线程来竞
 * 争资源，那CPU将会花费大量的时间和资源来处理这些竞争（事实上CPU的主要工作并非这些），同时，还有可能出现一些例如死锁之类的情况，最后，其实
 * 锁机制是一种比较粗糙，粒度比较大的机制，相对于像计数器这样的需求有点儿过于笨重。
 * 实现原子操作还可以使用当前的处理器基本都支持CAS()的指令，只不过每个厂家所实现的算法并不一样罢了，每一个CAS操作过程都包含三个运算符：一个
 * 内存地址V，一个期望的值A和一个新值B，操作的时候如果这个地址上存放的值等于这个期望的值A，则将地址上的值赋为新值B，否则不做任何操作。CAS的
 * 基本思路就是，如果这个地址上的值和期望的值相等，则给其赋予新值，否则不做任何事儿，但是要返回原值是多少。循环CAS就是在一个循环里不断的做CAS
 * 操作，直到成功为止。
 * <p>
 * volatile保证可见性，CAS保证原子性，就有了Atomic开头的类，Atomic操作类的底层正是用到了“CAS机制”。
 * 怎么实现线程安全呢？
 * 语言层面不做处理，我们将其交给硬件—CPU和内存，利用CPU的多处理能力，实现硬件层面的阻塞，再加上volatile变量的特性即可实现基于原子操作的线程安全。
 * 所以java中的Atomic开头的类，里面保证可见性都是通过volatile实现的，而原子性是头痛Unsafe类。
 * Atomic操作类自增为例子：
 * 1.获取当前值。 就是使用volatile关键字来保证。
 * 2.当前值+1，计算出目标值。
 * 3.进行CAS操作，如果成功则跳出循环，如果失败则重复上述步骤。
 * <p>
 * 从思想上来说，synchronized属于悲观锁，悲观的认为程序中的并发情况严重，所以严防死守，CAS属于乐观锁，乐观地认为程序中的并发情况不那么严重，
 * 所以让线程不断去重试更新。
 * CAS的缺点：
 * 1. CPU开销过大。自旋CAS如果长时间不成功，会给CPU带来非常大的执行开销。
 * 2. 只能保证一个共享变量的原子操作。当对一个共享变量执行操作时，我们可以使用循环CAS的方式来保证原子操作，但是对多个共享变量操作时，循环CAS
 * 就无法保证操作的原子性，这个时候就可以用锁。还有一个取巧的办法，就是把多个共享变量合并成一个共享变量来操作。比如，有两个共享变量i＝2，j=a，
 * 合并一下ij=2a，然后用CAS来操作ij。从Java 1.5开始，JDK提供了AtomicReference类来保证引用对象之间的原子性，就可以把多个变量放在一个对象
 * 里来进行CAS操作。
 * 3. ABA问题。因为CAS需要在操作值的时候，检查值有没有发生变化，如果没有发生变化则更新，但是如果一个值原来是A，变成了B，又变成了A，那么使用CAS
 * 进行检查时会发现它的值没有发生变化，但是实际上却变化了。ABA问题的解决思路就是使用版本号。在变量前面追加上版本号，每次变量更新的时候把版本号加1，
 * 那么A→B→A就会变成1A→2B→3A。
 */
class Atomic {

    private static AtomicInteger number = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        //不能保证原子性
        Atomic test = new Atomic();
        CountThread countThread1 = new CountThread(test);
        CountThread countThread2 = new CountThread(test);
        countThread1.start();
        countThread2.start();
        Thread.sleep(50);
        System.out.println(number);
    }

    public void addNumber() {
        // synchronized (Atomic.class){
        //     number++;
        // }
        number.incrementAndGet();
    }

    private static class CountThread extends Thread {

        private Atomic opera;

        public CountThread(Atomic opera) {
            this.opera = opera;
        }

        @Override
        public void run() {
            super.run();
            for (int i = 0; i < 1000; i++) {
                synchronized (Atomic.class) {
                    opera.addNumber();
                }
            }
        }
    }

}
