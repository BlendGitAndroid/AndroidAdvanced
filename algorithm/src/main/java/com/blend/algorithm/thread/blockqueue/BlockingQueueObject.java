package com.blend.algorithm.thread.blockqueue;

import java.util.LinkedList;

// 用 Object 实现生产者/消费者模式
class BlockingQueueObject {
    private int maxSize;
    private LinkedList<Object> storage;

    public BlockingQueueObject(int size) {
        this.maxSize = size;
        storage = new LinkedList<>();
    }

    public synchronized void put() throws InterruptedException {
        while (storage.size() == maxSize) {
            wait();
        }
        storage.add(new Object());
        notifyAll();
    }

    public synchronized void take() throws InterruptedException {
        while (storage.size() == 0) {
            wait();
        }
        System.out.println(storage.remove());
        notifyAll();
    }
}
