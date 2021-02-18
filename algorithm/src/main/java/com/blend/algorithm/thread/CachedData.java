package com.blend.algorithm.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁的升降级：
 * 降级：写锁 ->读锁
 * 升级：读锁 ->写锁，要等待读锁释放了才行，否则要是有多个读线程同时申请写锁，会发生死锁。
 * 升降级策略：只能从写锁降级为读锁，不能从读锁升级为写锁。
 */
class CachedData {

    Object data;
    volatile boolean cacheValid;
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    void processCachedData() {
        rwl.readLock().lock();
        if (!cacheValid) {
            //在获取写锁之前，必须首先释放读锁。
            rwl.readLock().unlock();
            rwl.writeLock().lock();
            try {
                //这里需要再次判断数据的有效性,因为在我们释放读锁和获取写锁的空隙之内，可能有其他线程修改了数据。
                if (!cacheValid) {
                    data = new Object();
                    cacheValid = true;
                }
                //在不释放写锁的情况下，直接获取读锁，这就是读写锁的降级。
                rwl.readLock().lock();
            } finally {
                //释放了写锁，但是依然持有读锁
                rwl.writeLock().unlock();
            }
        }

        try {
            System.out.println(data);
        } finally {
            //释放读锁
            rwl.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        new CachedData().processCachedData();
    }
}
