package com.blend.algorithm.thread;

/**
 * join方法：把指定的线程加入到当前线程，可以将两个交替执行的线程合并为顺序执行的线程。
 * 比如在线程B中调用了线程A的Join()方法，直到线程A执行完毕后，才会继续执行线程B。
 */
class JoinThread {

    public static void main(String[] args) throws InterruptedException {
        Thread previous = Thread.currentThread();   //主线程
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new JumpQueue(previous), String.valueOf(i));
            thread.start();
            previous = thread;
        }
        Thread.sleep(2000);
        System.out.println(Thread.currentThread().getName() + " terminated");
    }

    // 将传入的线程加入到新建的线程中,保证线程的执行书顺序
    private static class JumpQueue implements Runnable {

        private Thread mThread;

        public JumpQueue(Thread thread) {
            this.mThread = thread;
        }

        @Override
        public void run() {
            try {
                System.out.println(mThread.getName() + " will be join before "
                        + Thread.currentThread().getName());
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " terminated.");
        }

    }

}
