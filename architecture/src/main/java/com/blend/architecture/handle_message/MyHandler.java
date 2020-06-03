package com.blend.architecture.handle_message;

public class MyHandler {

    private MyMessageQueue mMessageQueue;
    private MyLooper mLooper;

    public MyHandler() {
        mLooper = MyLooper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException("Can't create handler inside thread " + Thread.currentThread()
                    + " that has not called Looper.prepare()");
        }
        mMessageQueue = mLooper.mQueue;
    }

    public void handleMessage(MyMessage message) {

    }

    public void sendMessage(MyMessage message) {
        enqueueMessage(message);
    }

    private void enqueueMessage(MyMessage message) {
        message.target = this;
        mMessageQueue.enqueueMessage(message);
    }
}
