package com.blend.algorithm.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 什么是线程池？
 * Java中的线程池是运用场景最多的并发框架，几乎所有需要异步或并发执行任务的程序都可以使用线程池。线程池就是将线程进行池化，
 * 需要运行任务时从池中拿一个线程来执行，执行完毕，线程放回池中。
 * <p>
 * 为什么要用线程池？
 * 1.降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。
 * 2。提高响应速度。当任务到达时，任务可以不需要等到线程创建就能立即执行。假设一个服务器完成一项任务所需时间为：T1 创建线
 * 程时间，T2 在线程中执行任务的时间，T3 销毁线程时间。如果：T1 + T3 远大于 T2，则可以采用线程池，以提高服务器性能。
 * 线程池技术正是关注如何缩短或调整T1,T3时间的技术，从而提高服务器程序性能的。它把T1，T3分别安排在服务器程序的启动和结束的
 * 时间段或者一些空闲的时间段，这样在服务器程序处理客户请求时，不会有T1，T3的开销了。
 * 3.提高线程的可管理性。线程是稀缺资源，如果无限制地创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一
 * 分配、调优和监控。
 * <p>
 * 线程池的参数：
 * 1.corePoolSize：线程池中的核心线程数，当提交一个任务时，线程池创建一个新线程执行任务，直到当前线程数等于corePoolSize；
 * 如果当前线程数为corePoolSize，继续提交的任务被保存到阻塞队列中，等待被执行；如果执行了线程池的prestartAllCoreThreads()
 * 方法，线程池会提前创建并启动所有核心线程。
 * 2.maximumPoolSize：线程池中允许的最大线程数。如果当前阻塞队列满了，且继续提交任务，则创建新的线程执行任务，前提是当前线程数
 * 小于maximumPoolSize。
 * 3.keepAliveTime：线程空闲时的存活时间，即当线程没有任务执行时，继续存活的时间。默认情况下，该参数只在线程数大于corePoolSize
 * 时才有用。
 * 4.TimeUnit：keepAliveTime的时间单位。
 * 5.workQueue：workQueue必须是BlockingQueue阻塞队列。当线程池中的线程数超过它的corePoolSize的时候，线程会进入阻塞队列进行
 * 阻塞等待。通过workQueue，线程池实现了阻塞功能。
 * 6.threadFactory：创建线程的工厂，通过自定义的线程工厂可以给每个新建的线程设置一个具有识别度的线程名Executors静态工厂里默认
 * 的threadFactory，线程的命名规则是“pool-数字-thread-数字”
 * 7.RejectedExecutionHandler(饱和策略)：线程池的饱和策略，当阻塞队列满了，且没有空闲的工作线程，如果继续提交任务，必须采取一种
 * 策略处理该任务，线程池提供了4种策略：
 * 1)AbortPolicy：直接抛出异常，默认策略；
 * 2)CallerRunsPolicy：用调用者所在的线程来执行任务；
 * 3)DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务；
 * 4)DiscardPolicy：直接丢弃任务；
 * 当然也可以根据应用场景实现RejectedExecutionHandler接口，自定义饱和策略，如记录日志或持久化存储不能处理的任务。
 * <p>
 * 线程池的工作机制：
 * 1.如果当前运行的线程少于corePoolSize，则创建新线程来执行任务（注意，执行这一步骤需要获取全局锁）。
 * 2.如果运行的线程等于或多于corePoolSize，则将任务加入BlockingQueue。
 * 3.如果无法将任务加入BlockingQueue（队列已满），则创建新的线程来处理任务（注意，执行这一步骤需要获取全局锁）。
 * 4.如果创建新线程将使当前运行的线程超出maximumPoolSize，任务将被拒绝，并调用RejectedExecutionHandler.rejectedExecution()方法。
 * <p>
 * 合理配置线程池：要想合理地配置线程池，就必须首先分析任务特性。要想合理地配置线程池，就必须首先分析任务特性，可以从以下几个角度来分析：
 * 1.任务的性质：CPU密集型任务、IO密集型任务和混合型任务。
 * 2.任务的优先级：高、中和低。
 * 3.任务的执行时间：长、中和短。
 * 4.任务的依赖性：是否依赖其他系统资源，如数据库连接。
 * 性质不同的任务可以用不同规模的线程池分开处理。CPU密集型任务应配置尽可能小的线程，如配置Ncpu+1个线程的线程池。由于IO密集型任务线程并不
 * 是一直在执行任务，则应配置尽可能多的线程，如2*Ncpu。混合型的任务，如果可以拆分，将其拆分成一个CPU密集型任务和一个IO密集型任务，只要这
 * 两个任务执行的时间相差不是太大，那么分解后执行的吞吐量将高于串行执行的吞吐量。如果这两个任务执行时间相差太大，则没必要进行分解。
 * 可以通过Runtime.getRuntime().availableProcessors()方法获得当前设备的CPU个数。
 * <p>
 * 阻塞队列：阻塞队列常用于生产者和消费者的场景，生产者是向队列里添加元素的线程，消费者是从队列里取元素的线程。阻塞队列就是生产者用来存放元素、
 * 消费者用来获取元素的容器。
 * 1.支持阻塞的插入方法：意思是当队列满时，队列会阻塞插入元素的线程，直到队列不满。
 * 2.支持阻塞的移除方法：意思是在队列为空时，获取元素的线程会等待队列变为非空。
 * 方法对比：
 * 1.add()/remove()/element()：抛出异常。当队列满时，如果再往队列里插入元素，会抛出IllegalStateException（"Queuefull"）异常。当队列空
 * 时，从队列里获取元素会抛出NoSuchElementException异常。
 * 2.offer()/poll()/peek()：返回特殊值。当往队列插入元素时，会返回元素是否插入成功，成功返回true。如果是移除方法，则是从队列里取出一个元素，
 * 如果没有则返回null。
 * 3.put()/take()：一直阻塞：当阻塞队列满时，如果生产者线程往队列里put元素，队列会一直阻塞生产者线程，直到队列可用或者响应中断退出。当队列空时，
 * 如果消费者线程从队列里take元素，队列会阻塞住消费者线程，直到队列不为空。
 * 常用的阻塞队列：
 * 1.ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。
 * 2.LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。
 * 3.PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列。
 * 4.DelayQueue：一个使用优先级队列实现的无界阻塞队列。
 * 5.SynchronousQueue：一个不存储元素的阻塞队列。
 * 6.LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
 * 7.LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
 */
class ThreadPool {


    private static class DispatcherExecutor {

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
}
