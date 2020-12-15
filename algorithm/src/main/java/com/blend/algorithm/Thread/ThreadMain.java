package com.blend.algorithm.Thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * 多核心：也指单芯片多处理器，将大规模并行处理器集成到同一芯片内。
 * 核心数、线程数:目前主流CPU有双核、三核和四核,六核也在2010年发布。增加核心数目就是为了增加线程数,因为操作系统是通过
 * 线程来执行任务的,一般情况下它们是1:1对应关系,也就是说四核CPU一般拥有四个线程。但 Intel引入超线程技术后,使核心数与
 * 线程数形成1:2的关系。
 * <p>
 * CPU时间片轮转机制：RR调度。每个进程被分配一个时间段,称作它的时间片,即该进程允许运行的时间。
 * 线程切换：有时也称作上下文切换。
 * 时间片轮转调度中唯一有趣的一点是时间片的长度。时间片设得太短会导致过多的进程切换,降低了CPU效率:而设得太长又可能引起
 * 对短的交互请求的响应变差。将时间片设为100ms通常是一个比较合理的折衷。
 * <p>
 * 进程是程序运行资源分配的最小单位，其中资源包括:CPU、内存空间、磁盘10等。
 * 线程是CPU调度的最小单位,必须依赖于进程而存在，线程自己基本上不拥有系统资源,只拥有一点在运行中必不可少的资源(如程序计
 * 数器,一组寄存器和栈),但是它可与同属一个进程的其他的线程共享进程所拥有的全部资源。
 * <p>
 * 并行和并发：
 * 当谈论并发的时候一定要加个单位时间,也就是说单位时间内并发量是多少?离开了单位时间其实是没有意义的。
 * 一个是交替执行,一个是同时执行。
 * <p>
 * 高并发编程的意义：
 * 1.充分利用CPU的资源。
 * 2.加快用户响应时间。
 * 注意事项：
 * 1.线程之间的安全性。多线程对同一个变量的读写。
 * 2.线程之间的死锁。
 * 3.线程数目过多将服务器资源耗尽形成死机和宕机。某些系统资源是有限的,如文件描述符。多线程程序可能耗尽资源,因为每个线程都
 * 可能希望有一个这样的资源。如果线程数相当大,或者某个资源的侯选线程数远远超过了可用的资源数则最好使用资源池。
 * <p>
 * java程序天生就是多线程：
 * [5] Attach Listener。内存dump，线程dump，类信息统计，获取系统属性等
 * [4] Signal Dispatcher。分发处理发送给JVM信号的线程
 * [3] Finalizer。调用对象finalize方法的线程
 * [2] Reference Handler。清除Reference的线程
 * [1] main。用户程序入口
 * <p>
 * 注意点：
 * 1.进程和CPU是没有关系的。
 * 2.一次线程的上下文切换，需要耗费20000个CPU时间周期。
 * 3.一个线程可以有多个Runnable，但是只有Thread是操作系统对线程的抽象。
 * 4.sleep()可以中止，调用interrupt()中止。
 * 5.sleep和wait的区别。wait就是等到资源满足调用notify/notifyAll来唤醒；sleep则等待时间结束。
 * 6.线程的优先级和操作系统的优先级对不上号，不要指望使用线程的优先级来保证线程的执行顺序。
 * 7.保证线程的执行顺序应该使用：join或者CountDownLatch。
 * <p>
 * 额外：
 * 4.线程的所有方法有哪些？
 * join()：把指定的线程加入到当前线程，可以将两个交替执行的线程合并为顺序执行的线程。
 * yield()：使当前线程让出CPU占有权，但让出的时间是不可设定的。也不会释放锁资源，所有执行yield()的线程有可能在进入到可执行状态后马上又被执行。
 */
class ThreadMain {
    public static void main(String[] args) {

        //天生多线程
        more();
    }

    private static void more() {
        /*虚拟机线程管理接口*/
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        /*取得线程信息*/
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("[" + threadInfo.getThreadId() + "]" + " "
                    + threadInfo.getThreadName());
        }
    }
}
