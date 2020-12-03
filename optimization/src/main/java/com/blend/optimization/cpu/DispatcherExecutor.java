package com.blend.optimization.cpu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DispatcherExecutor {

    /**
     * CPU 密集型任务的线程池
     */
    private static ThreadPoolExecutor sCPUThreadPoolExecutor;

    /**
     * IO 密集型任务的线程池
     */
    private static ExecutorService sIOThreadPoolExecutor;

    /**
     * 当前设备可以使用的 CPU 核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池核心线程数，其数量在2 ~ 5这个区域内
     */
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 5));

    /**
     * 线程池线程数的最大值：这里指定为了核心线程数的大小
     */
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;

    /**
     * 线程池中空闲线程等待工作的超时时间，当线程池中
     * 线程数量大于corePoolSize（核心线程数量）或
     * 设置了allowCoreThreadTimeOut（是否允许空闲核心线程超时）时，
     * 线程会根据keepAliveTime的值进行活性检查，一旦超时便销毁线程。
     * 否则，线程会永远等待新的工作。
     */
    private static final int KEEP_ALIVE_SECONDS = 5;

    /**
     * 创建一个基于链表节点的阻塞队列
     */
    private static final BlockingQueue<Runnable> S_POOL_WORK_QUEUE = new LinkedBlockingQueue<>();

    /**
     * 用于创建线程的线程工厂
     */
    private static final DefaultThreadFactory S_THREAD_FACTORY = new DefaultThreadFactory();

    /**
     * 线程池执行耗时任务时发生异常所需要做的拒绝执行处理
     * 注意：一般不会执行到这里
     */
    private static final RejectedExecutionHandler S_HANDLER = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Executors.newCachedThreadPool().execute(r);
        }
    };

    /**
     * 获取CPU线程池
     *
     * @return CPU线程池
     */
    public static ThreadPoolExecutor getCPUExecutor() {
        return sCPUThreadPoolExecutor;
    }

    /**
     * 获取IO线程池
     *
     * @return IO线程池
     */
    public static ExecutorService getIOExecutor() {
        return sIOThreadPoolExecutor;
    }

    /**
     * 实现一个默认的线程工厂
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            //当运行未知的Java程序的时候，该程序可能有恶意代码（删除系统文件、重启系统等），为了防止运行恶意代码对
            // 系统产生影响，需要对运行的代码的权限进行控制，这时候就要启用Java安全管理器。
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            //设置线程名字，以便于维护
            namePrefix = "TaskDispatcherPool-" +
                    POOL_NUMBER.getAndIncrement() +
                    "-Thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            // 每一个新创建的线程都会分配到线程组group当中
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                // 非守护线程
                t.setDaemon(false);
            }
            // 设置线程优先级
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    static {
        sCPUThreadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                S_POOL_WORK_QUEUE, S_THREAD_FACTORY, S_HANDLER);
        // 设置是否允许空闲核心线程超时时，线程会根据keepAliveTime的值进行活性检查，一旦超时便销毁线程。否则，线程会永远等待新的工作。
        sCPUThreadPoolExecutor.allowCoreThreadTimeOut(true);
        // IO密集型任务线程池直接采用CachedThreadPool来实现，
        // 它最多可以分配Integer.MAX_VALUE个非核心线程用来执行任务
        sIOThreadPoolExecutor = Executors.newCachedThreadPool(S_THREAD_FACTORY);
    }

}

