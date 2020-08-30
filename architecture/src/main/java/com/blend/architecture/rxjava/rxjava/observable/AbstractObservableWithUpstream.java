package com.blend.architecture.rxjava.rxjava.observable;


import com.blend.architecture.rxjava.rxjava.Observable;
import com.blend.architecture.rxjava.rxjava.ObservableSource;

//被观察者
public abstract class AbstractObservableWithUpstream<T, U> extends Observable<U> {
    protected final ObservableSource<T> source;

    public AbstractObservableWithUpstream(ObservableSource<T> source) {
        this.source = source;
    }
}
