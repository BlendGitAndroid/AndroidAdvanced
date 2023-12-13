package com.blend.algorithm.thread;

/**
 * ThreadLocal，即线程本地变量，是一个以ThreadLocal对象为键、任意对象为值的存储结构。
 * 具体的意思就是：一个ThreadLocal对象，会获取当前的线程，并根据当前线程获取该线程的ThreadLocalMapd对象，
 * 这个对象的key就是ThreadLocal对象，value就是存储的值。
 * 这个结构被附带在线程上，也就是说一个线程可以根据一个ThreadLocal对象查询到绑定在这个线程上的一个值, ThreadLocal
 * 往往用来实现变量在线程之间的隔离。
 * <p>
 * 一个线程想要存储多个线程独享的对象，那么就new多个ThreadLocaL对象。
 * 这里需要重点看到它们的数量对应关系：一个 Thread 里面只有一个ThreadLocalMap ，而在一个 ThreadLocalMap 里面却可以有
 * 很多的 ThreadLocal，每一个 ThreadLocal 都对应一个 value。因为一个 Thread 是可以调用多个 ThreadLocal 的，
 * 所以 Thread 内部就采用了 ThreadLocalMap 这样 Map 的数据结构来存放 ThreadLocal 和 value。
 * <p>
 * 一个ThreadLocal对象还能被多个线程使用，因为每一个线程都有自己的Map对象，虽然存储的key是一样的，但是map是不一样的。
 * <p>
 * ThreadLocal类接口很简单，只有4个方法:
 * 1)void set(Object value):设置当前线程的线程局部变量的值。
 * 2)public Object get():该方法返回当前线程所对应的线程局部变量。
 * 3)public void remove():将当前线程局部变量的值删除，目的是为了减少内存的占用，该方法是JDK 5.0新增的方法。需要指出的是，
 * 当线程结束后，对应该线程的局部变量将自动被垃圾回收，所以显式调用该方法清除线程的局部变量并不是必须的操作，但它可以加快内存回收的速度。
 * 4)protected Object initialValue():返回该线程局部变量的初始值，该方法是一个protected的方法，显然是为了让子类覆盖而设计的。这
 * 个方法是一个延迟调用方法，在线程第1次调用get()或set(Object)时才执行，并且仅执行1次。ThreadLocal中的缺省实现直接返回一个null。
 * <p>
 * 使用：
 * public final static ThreadLocal<String> RESOURCE = new ThreadLocal<String>();RESOURCE代表一个能够存放String类型的
 * ThreadLocal对象。此时不论什么一个线程能够并发访问这个变量，对它进行写入、读取操作，都是线程安全的。
 * <p>
 * <p>
 * ThreadLocal就一个作用，保存每个线程独享的对象。ThreadLocal是解决线程安全问题的，但是不是解决资源共享问题的。
 * <p>
 * 内存泄漏问题：假如一个Activity中有一个ThreadLocal对象，当Activity销毁时，那么对ThreadLocal的强引用就会断开，那ThreadLocal只有一个
 * 弱引用了，那么就会被垃圾回收掉。但是你要说，Entry还引用这个弱引用了呢？对啊，你都说是弱引用了，所以会被销毁。但是value没有被销毁，这个时候
 * 就会出现内存泄漏。
 */
class ThreadLocalTest {

    public static void main(String[] args) {
        ThreadLocalTest test = new ThreadLocalTest();
        test.StartThreadArray();
    }

    static AA a = new AA();

    static ThreadLocal<AA> threadLocal = new ThreadLocal<AA>() {
        @Override
        protected AA initialValue() {
            System.out.println(Thread.currentThread().getName() + ":initialValue");
            return new AA();
        }
    };

    static class AA {
        int a = 0;
    }


    /**
     * 运行3个线程
     */
    public void StartThreadArray() {
        Thread[] runs = new Thread[3];
        for (int i = 0; i < runs.length; i++) {
            runs[i] = new Thread(new TestRunnable(i));
        }
        for (int i = 0; i < runs.length; i++) {
            runs[i].start();
        }
    }

    /**
     * 类说明：测试线程，线程的工作是将ThreadLocal变量的值变化，并写回，
     * 看看线程之间是否会互相影响
     */
    public static class TestRunnable implements Runnable {
        int id;

        public TestRunnable(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println(Thread.currentThread().getName() + ":start");
            AA s = threadLocal.get();
            s.a = s.a + id;
            threadLocal.set(s);
            System.out.println(Thread.currentThread().getName() + " :"
                    + threadLocal.get().a);
            threadLocal.remove();
        }
    }


}
