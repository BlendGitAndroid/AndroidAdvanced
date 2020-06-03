package com.blend.architecture.handle_message;

public class MyLooper {

    static final ThreadLocal<MyLooper> sThreadLocal = new ThreadLocal<>();
    MyMessageQueue mQueue;

    private MyLooper() {
        mQueue = new MyMessageQueue();
    }

    public static void prepare() {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one DnLooper may be created per thread");
        }
        sThreadLocal.set(new MyLooper());
    }

    public static MyLooper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop() {
        MyMessageQueue queue = myLooper().mQueue;
        while (true) {
            MyMessage message = queue.next();
            if (message != null) {
                message.target.handleMessage(message);
            }
        }
    }

}
