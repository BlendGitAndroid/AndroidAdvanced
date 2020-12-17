package com.blend.algorithm.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 任意一个Java对象，都拥有一组监视器方法（定义在java.lang.Object上），主要包括wait()、wait(long timeout)、notify()
 * 以及notifyAll()方法，这些方法与synchronized同步关键字配合，可以实现等待/通知模式。
 * Condition接口也提供了类似Object的监视器方法，与Lock配合可以实现等待/通知模式。
 * await(),signal(),signalAll()等方法配合使用。
 */
class ConditionNotify {
    private static Express express = new Express(0, Express.CITY);

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new CheckSite().start();
        }
        for (int i = 0; i < 3; i++) {
            new CheckKm().start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        express.changeKm();//快递里程变化
    }

    /*检查里程数变化的线程,不满足条件，线程一直等待*/
    private static class CheckKm extends Thread {
        @Override
        public void run() {
            super.run();
            express.waitKm();
        }
    }

    /*检查地点变化的线程,不满足条件，线程一直等待*/
    private static class CheckSite extends Thread {
        @Override
        public void run() {
            express.waitSite();
        }
    }

    private static class Express {

        private final static String CITY = "ShangHai";
        private int km;
        private String site;
        private Lock lock = new ReentrantLock();
        private Condition kmCondition = lock.newCondition();
        private Condition siteCondition = lock.newCondition();

        public Express(int km, String site) {
            this.km = km;
            this.site = site;
        }

        /*变化公里数，然后通知处于wait状态并需要处理公里数的线程进行业务处理*/
        public void changeKm() {
            lock.lock();
            try {
                this.km = 101;
                kmCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        /* 变化地点，然后通知处于wait状态并需要处理地点的线程进行业务处理*/
        public void changeSite() {
            lock.lock();
            try {
                this.site = "BeiJing";
                siteCondition.signal();
            } finally {
                lock.unlock();
            }
        }

        /*当快递的里程数大于100时更新数据库*/
        public void waitKm() {
            lock.lock();
            try {
                while (this.km < 100) {
                    try {
                        kmCondition.await();
                        System.out.println("check km thread[" + Thread.currentThread().getName() + "] is be notified");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("the Km is " + this.km + ",I will change db");
            } finally {
                lock.unlock();
            }
        }

        /*当快递到达目的地时通知用户*/
        public void waitSite() {
            lock.lock();
            try {
                while (CITY.equals(this.site)) {
                    try {
                        siteCondition.await();
                        System.out.println("check site thread[" + Thread.currentThread().getId()
                                + "] is be notified.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                lock.unlock();
            }
            System.out.println("the site is " + this.site + ",I will call user");
        }

    }

}
