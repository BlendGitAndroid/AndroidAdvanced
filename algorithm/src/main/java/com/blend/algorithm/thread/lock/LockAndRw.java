package com.blend.algorithm.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class LockAndRw implements GoodService {

    private GoodsInfo mGoodsInfo;
    private final ReadWriteLock mLock = new ReentrantReadWriteLock();
    private final Lock getLock = mLock.readLock();  //读锁
    private final Lock setLock = mLock.writeLock(); //写锁

    public LockAndRw(GoodsInfo goodsInfo) {
        mGoodsInfo = goodsInfo;
    }

    @Override
    public GoodsInfo getNum() {
        getLock.lock();
        try {
            Thread.sleep(5);
            return mGoodsInfo;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            getLock.unlock();   //不建议在finally内执行return语句
        }
        return null;
    }

    @Override
    public void setNum(int number) {
        setLock.lock();
        try {
            Thread.sleep(5);
            mGoodsInfo.changeNumber(number);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            setLock.unlock();
        }
    }
}
