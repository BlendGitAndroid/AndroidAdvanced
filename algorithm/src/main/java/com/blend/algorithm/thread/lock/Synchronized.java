package com.blend.algorithm.thread.lock;

class Synchronized implements GoodService {

    private GoodsInfo mGoodsInfo;

    public Synchronized(GoodsInfo goodsInfo) {
        mGoodsInfo = goodsInfo;
    }

    @Override
    public synchronized GoodsInfo getNum() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mGoodsInfo;
    }

    @Override
    public void setNum(int number) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mGoodsInfo.changeNumber(number);
    }
}
