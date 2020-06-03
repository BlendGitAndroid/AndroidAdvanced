package com.blend.architecture.handle_message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MyMessageQueue {

    BlockingQueue<MyMessage> blockingQueue = new ArrayBlockingQueue<MyMessage>(50);

    public void enqueueMessage(MyMessage message) {
        try {
            blockingQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MyMessage next() {
        try {
            blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
