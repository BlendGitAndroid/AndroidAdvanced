package com.blend.architecture.okhttp.okhttp;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * keep-alive 就是浏览器和服务端之间保持长连接，这个连接是可以复用的。在HTTP1.1中是默认开启的。
 *
 * 连接的复用为什么会提高性能呢？
 *
 *(一次响应的过程) 通常我们在发起http请求的时候首先要完成tcp的三次握手，然后传输数据，最后再释放连接
 *
 * 如果在高并发的请求连接情况下或者同个客户端多次频繁的请求操作，无限制的创建会导致性能低下。
 * 如果使用keep-alive，在timeout空闲时间内，连接不会关闭，相同重复的request将复用原先的connection，
 * 减少握手的次数，大幅提高效率。（并非keep-alive的timeout设置时间越长，就越能提升性能。
 * 长久不关闭会造成过多的僵尸连接和泄露连接出现）
 */
public class ConnectionPool {

    /**
     * 垃圾回收线程
     * 线程池，用来检测闲置socket并对其进行清理
     */
    private static ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread result = new Thread(runnable, "DNHttpClient ConnectionPool");
            result.setDaemon(true);
            return result;
        }
    };
    private static final Executor executor = new ThreadPoolExecutor(0 /* corePoolSize */,
            Integer.MAX_VALUE /* maximumPoolSize */, 60L /* keepAliveTime */, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), threadFactory);

    /**
     * 每个连接的最大存活时间
     */
    private final long keepAliveDuration;

    private final Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long waitTimes = cleanup(System.currentTimeMillis());
                if (waitTimes == -1) {
                    return;
                }
                if (waitTimes > 0) {
                    synchronized (ConnectionPool.this) {
                        try {
                            //调用某个对象的wait()方法能让当前线程阻塞，并且当前线程必须拥有此对象的monitor（即锁）
                            ConnectionPool.this.wait(waitTimes);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }
    };

    private final Deque<HttpConnection> connections = new ArrayDeque<>();
    private boolean cleanupRunning;

    public ConnectionPool() {
        this(1, TimeUnit.MINUTES);
    }

    public ConnectionPool(long keepAliveDuration, TimeUnit timeUnit) {
        //毫秒
        this.keepAliveDuration = timeUnit.toMillis(keepAliveDuration);
    }

    public HttpConnection get(String host, int port) {
        Iterator<HttpConnection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            HttpConnection connection = iterator.next();
            //查连接是否复用( 同样的host )
            if (connection.isSameAddress(host, port)) {
                //正在使用的移出连接池
                iterator.remove();
                return connection;
            }
        }
        return null;
    }


    public void put(HttpConnection connection) {
        //执行检测清理
        if (!cleanupRunning) {
            cleanupRunning = true;
            executor.execute(cleanupRunnable);
        }
        connections.add(connection);
    }


    /**
     * 检查需要移除的连接返回下次检查时间
     */
    long cleanup(long now) {
        long longestIdleDuration = -1;
        synchronized (this) {
            for (Iterator<HttpConnection> i = connections.iterator(); i.hasNext(); ) {
                HttpConnection connection = i.next();
                //获得闲置时间 多长时间没使用这个了
                long idleDuration = now - connection.lastUsetime;
                //如果闲置时间超过允许
                if (idleDuration > keepAliveDuration) {
                    connection.closeQuietly();
                    i.remove();
                    Log.e("Pool", "移出连接池");
                    continue;
                }
                //获得最大闲置时间
                if (longestIdleDuration < idleDuration) {
                    longestIdleDuration = idleDuration;
                }
            }
            //下次检查时间
            if (longestIdleDuration >= 0) {
                return keepAliveDuration - longestIdleDuration;
            } else {
                //连接池没有连接 可以退出
                cleanupRunning = false;
                return longestIdleDuration;
            }
        }
    }

}
