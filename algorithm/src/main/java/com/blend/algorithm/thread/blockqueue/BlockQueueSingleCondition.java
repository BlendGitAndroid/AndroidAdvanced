package com.blend.algorithm.thread.blockqueue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 其实我感觉用一个Condition就可以实现，只不过用两个Condition颗粒度更细
class BlockQueueSingleCondition<T> {

    private Queue<T> queue;
    private int maxSize;
    private Lock lock;
    private Condition condition;

    public BlockQueueSingleCondition(int maxSize) {
        this.queue = new LinkedList<>();
        this.maxSize = maxSize;
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public void enqueue(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == maxSize) {
                condition.await(); // 队列满了，等待
            }
            queue.add(item);
            condition.signalAll(); // 有新元素加入，唤醒等待的线程
        } finally {
            lock.unlock();
        }
    }

    public T dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                condition.await(); // 队列为空，等待
            }
            T item = queue.poll();
            condition.signalAll(); // 有元素被取出，唤醒等待的线程
            return item;
        } finally {
            lock.unlock();
        }
    }

}
