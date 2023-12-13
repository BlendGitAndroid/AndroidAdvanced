package com.blend.algorithm.thread;

/**
 * 等待通知机制：是指一个线程A调用了对象O的wait()方法进入等待状态，而另一个线程B调用了对象O的notify()或者notifyAll()方法，
 * 线程A收到通知后从对象O的wait()方法返回，进而执行后续操作。上述两个线程通过对象O来完成交互，而对象上的wait()和notify/notifyAll()
 * 的关系就如同开关信号一样，用来完成等待方和通知方之间的交互工作。
 * <p>
 * notify:通知一个在对象上等待的线程,使其从wait方法返回,而返回的前提是该线程获取到了对象的锁，没有获得锁的线程重新进入WAITING状态。
 * notifyAll:通知所有等待在该对象上的线程。
 * wait:调用该方法的线程进入WAITING状态,只有等待另外线程的通知或被中断才会返回.需要注意,调用wait()方法后,会释放对象的锁。
 * wait(long):超时等待一段时间,这里的参数时间是毫秒,也就是等待长达n毫秒,如果没有通知就超时返回。
 * wait(long,int):对于超时时间更细粒度的控制,可以达到纳秒。
 * <p>
 * 等待和通知的标准范式：
 * 等待方遵循如下规则：
 * 1)获取对象的锁。
 * 2)如果条件不满足，那么调用对象的wait()方法，被通知后仍要检查条件。
 * 3)条件满足则执行对应的逻辑。
 * 通知方遵循如下原则。
 * 1)获得对象的锁。
 * 2)改变条件。
 * 3)通知所有等待在对象上的线程。
 * <p>
 * 在调用wait（）之前，线程必须要获得该对象的对象级别锁，即只能在同步方法或同步块中调用wait（）方法，进入wait（）方法后，当前线程释放锁，
 * 在从wait（）返回前，线程与其他线程竞争重新获得锁，notifyAll方法一旦该对象锁被释放（notifyAll线程退出调用了notifyAll的synchronized
 * 代码块的时候），他们就会去竞争。如果其中一个线程获得了该对象锁，它就会继续往下执行，在它退出synchronized代码块，释放锁后，其他的已经被唤
 * 醒的线程将会继续竞争获取该锁，一直进行下去，直到所有被唤醒的线程都执行完毕。
 * <p>
 * 注意：wait/notify/notifyAll都是对于同一个对象来说的，它们都是Object方法。
 */
class WaitAndNotify {
    // 都是同一个对象
    private static Express express = new Express(0, Express.CITY);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {//三个线程,等待快递到达地点的变化
            new CheckSite().start();
        }
        for (int i = 0; i < 3; i++) {//三个线程,等待里程数的变化
            new CheckKm().start();
        }

        Thread.sleep(1000);
        //这一行代码会通知该对象所有的wait方法，所以site也会被通知到，但是条件不满足，继续wait
        express.changeKm();//快递里程数变化
    }

    /*检查里程数变化的线程,不满足条件，线程一直等待*/
    private static class CheckKm extends Thread {
        @Override
        public void run() {
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
        public final static String CITY = "ShangHai";
        private int km;/*快递运输里程数*/
        private String site;/*快递到达地点*/

        public Express() {
        }

        public Express(int km, String site) {
            this.km = km;
            this.site = site;
        }

        /* 变化公里数，然后通知处于wait状态并需要处理公里数的线程进行业务处理*/
        public synchronized void changeKm() {
            this.km = 101;
            notifyAll();
        }

        /* 变化地点，然后通知处于wait状态并需要处理地点的线程进行业务处理*/
        public synchronized void changeSite() {
            this.site = "BeiJing";
            notify();
        }

        public synchronized void waitKm() {
            while (this.km < 100) { //这里使用while循环，就是为了防止调用notifyAll后，km不满足，但是也会被通知到
                try {
                    System.out.println("-----km---");
                    wait();
                    System.out.println("check km thread["
                            + Thread.currentThread().getName() + "] is be notifyed");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("the km is" + this.km + ",I will change db.");

        }

        public synchronized void waitSite() {
            while (CITY.equals(this.site)) {
                try {
                    System.out.println("-----site-----");
                    wait();
                    System.out.println("check site thread["
                            + Thread.currentThread().getName() + "] is be notifyed");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("the site is" + this.site + ",I will call user.");
        }
    }
}
