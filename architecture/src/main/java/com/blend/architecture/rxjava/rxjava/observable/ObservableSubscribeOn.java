package com.blend.architecture.rxjava.rxjava.observable;



import com.blend.architecture.rxjava.rxjava.Disposeble;
import com.blend.architecture.rxjava.rxjava.Observer;
import com.blend.architecture.rxjava.rxjava.ObserverbleSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//指定被观察者在那个线程运行的被观察者
public class ObservableSubscribeOn<T> extends AbstractObservableWithUpstream<T, T>{
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public ObservableSubscribeOn(ObserverbleSource<T> source) {
        super(source);
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        //重新包装observer
        final SubscribeOnObserver parent = new SubscribeOnObserver(observer);
        //告诉下游观察者订阅成功
        observer.onSubscribe(parent);

        //开始线程切换
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                source.subscribe(parent);
            }
        });
    }


    static final class SubscribeOnObserver<T> implements Observer<T>, Disposeble{
        final Observer<? super  T> actual;
        Disposeble<T> disposeble;

        public SubscribeOnObserver(Observer<? super T> actual) {
            this.actual = actual;
        }

        @Override
        public void disposa(boolean bool) {
            this.disposeble.disposa(bool);
        }

        @Override
        public boolean isDisposad() {
            return this.disposeble.isDisposad();
        }

        @Override
        public void onSubscribe(Disposeble d) {
            this.disposeble = d;
        }

        @Override
        public void onNext(T t) {
            this.actual.onNext(t);
        }

        @Override
        public void onError(Throwable e) {
            this.actual.onError(e);
        }

        @Override
        public void onComplete() {
            this.actual.onComplete();
        }
    }
}
