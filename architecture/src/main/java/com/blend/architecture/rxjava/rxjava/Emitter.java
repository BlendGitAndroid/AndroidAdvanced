package com.blend.architecture.rxjava.rxjava;

public interface Emitter<T> {
    //发出正常值信号
    void onNext(T value);
    //发出一个Throwable异常信号
    void onError(Throwable throwable);
    //发出完成的信号
    void onComplete();
}
