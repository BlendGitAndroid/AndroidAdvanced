package com.blend.architecture.okhttp.okhttp;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Dispatcher {
    //最多同时请求
    private int maxRequests;
    //同一个host同时最多请求
    private int maxRequestsPerHost;

    public Dispatcher() {
        this(64, 2);
    }

    public Dispatcher(int maxRequests, int maxRequestsPerHost) {
        this.maxRequests = maxRequests;
        this.maxRequestsPerHost = maxRequestsPerHost;
    }

    private ExecutorService executorService;

    /**
     * 等待执行队列
     */
    private final Deque<Call.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    /**
     * 正在执行队列
     */
    private final Deque<Call.AsyncCall> runningAsyncCalls = new ArrayDeque<>();


    /**
     * 线程池
     *
     * @return
     */
    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread result = new Thread(runnable, "OkHttp Dispatcher");
                    return result;
                }
            };
            /**
             *    1、corePoolSize：线程池中核心线程数的最大值
             *    2、maximumPoolSize：线程池中能拥有最多线程数
             *    3、keepAliveTime：表示空闲线程的存活时间  60秒
             *    4、表示keepAliveTime的单位。
             *    5、workQueue：它决定了缓存任务的排队策略。
             *      SynchronousQueue<Runnable>：此队列中不缓存任何一个任务。向线程池提交任务时，
             *      如果没有空闲线程来运行任务，则入列操作会阻塞。当有线程来获取任务时，
             *      出列操作会唤醒执行入列操作的线程。
             *    6、指定创建线程的工厂
             */
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), threadFactory);
        }
        return executorService;
    }


    public void enqueue(Call.AsyncCall call) {
        //不能超过最大请求数，同时执行相同的host请求不能超过最大host数
        Log.e("Dispatcher", "同时有:" + runningAsyncCalls.size());
        Log.e("Dispatcher", "host同时有:" + runningCallsForHost(call));
        if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) <
                maxRequestsPerHost) {
            Log.e("Dispatcher", "提交执行");
            runningAsyncCalls.add(call);
            executorService().execute(call);
        } else {
            Log.e("Dispatcher", "等待执行");
            readyAsyncCalls.add(call);
        }
    }

    /**
     * 同一host 的 同时请求数
     *
     * @param call
     * @return
     */
    private int runningCallsForHost(Call.AsyncCall call) {
        int result = 0;
        //如果执行这个请求，则相同的host数量是result
        for (Call.AsyncCall c : runningAsyncCalls) {
            if (c.host().equals(call.host())) {
                result++;
            }
        }
        return result;
    }


    /**
     * 请求结束 移出正在运行队列
     * 并判断是否执行等待队列中的请求
     *
     * @param asyncCall
     */
    public void finished(Call.AsyncCall asyncCall) {
        synchronized (this) {
            runningAsyncCalls.remove(asyncCall);
            //判断是否执行等待队列中的请求
            promoteCalls();
        }
    }

    /**
     * 判断是否执行等待队列中的请求
     */
    private void promoteCalls() {
        //同时请求达到上限
        if (runningAsyncCalls.size() >= maxRequests) {
            return;
        }
        //没有等待执行请求
        if (readyAsyncCalls.isEmpty()) {
            return;
        }
        for (Iterator<Call.AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            Call.AsyncCall call = i.next();
            //同一host同时请求为达上限
            if (runningCallsForHost(call) < maxRequestsPerHost) {
                i.remove();
                runningAsyncCalls.add(call);
                executorService().execute(call);
            }
            //到达同时请求上限
            if (runningAsyncCalls.size() >= maxRequests) {
                return;
            }
        }

    }
}
