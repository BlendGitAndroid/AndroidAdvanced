package com.blend.algorithm.thread;

/**
 * ThreadLocal，即线程本地变量，是一个以ThreadLocal对象为键、任意对象为值的存储结构。这个结构被附带在线程上，也就是说一个
 * 线程可以根据一个ThreadLocal对象查询到绑定在这个线程上的一个值, ThreadLocal往往用来实现变量在线程之间的隔离。
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
 */
class ThreadLocalTest {

    public static void main(String[] args) {
        ThreadLocalTest test = new ThreadLocalTest();
        test.StartThreadArray();
    }

    static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 1;
        }
    };


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
            Integer s = threadLocal.get();
            s = s + id;
            threadLocal.set(s);
            System.out.println(Thread.currentThread().getName() + " :"
                    + threadLocal.get());
            //threadLocal.remove();
        }
    }


}
