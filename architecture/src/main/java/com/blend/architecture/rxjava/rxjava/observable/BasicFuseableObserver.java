package com.blend.architecture.rxjava.rxjava.observable;


import com.blend.architecture.rxjava.rxjava.Disposeble;
import com.blend.architecture.rxjava.rxjava.Observer;

//观察者
public abstract class BasicFuseableObserver<T, R> implements Observer<T>, Disposeble<R> {
    //观察者
    protected final Observer<? super R> actual;

    protected Disposeble disposeble;

    public BasicFuseableObserver(Observer<? super R> actual) {
        this.actual = actual;
    }

    @Override
    public void onSubscribe(Disposeble d) {
        this.disposeble = d;
        actual.onSubscribe(d);
    }

    @Override
    public void onError(Throwable e) {
        actual.onError(e);
    }

    @Override
    public void onComplete() {
        actual.onComplete();
    }

    @Override
    public void disposa(boolean bool) {
        disposeble.disposa(bool);
    }

    @Override
    public boolean isDisposad() {
        return disposeble.isDisposad();
    }
}
