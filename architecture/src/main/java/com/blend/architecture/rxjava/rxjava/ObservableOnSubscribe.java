package com.blend.architecture.rxjava.rxjava;

//为被观察者定义发送数据的发射器
public interface ObservableOnSubscribe<T> {

    //为每个订阅的观察者调用
    void subscribe(ObservableEmitter<T> emitter) throws Exception;
}
