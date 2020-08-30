package com.blend.architecture.rxjava.rxjava.observable;

import android.os.Handler;
import android.os.Looper;

import com.blend.architecture.rxjava.rxjava.Disposeble;
import com.blend.architecture.rxjava.rxjava.Observer;
import com.blend.architecture.rxjava.rxjava.ObserverbleSource;


//指定观察者在那个线程运行的被观察者
public class ObservableObserveOn<T> extends AbstractObservableWithUpstream<T, T> {

    public ObservableObserveOn(ObserverbleSource<T> source) {
        super(source);
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        ObserverOnObserver<T> parent = new ObserverOnObserver<>(observer);
        source.subscribe(parent);
    }


    //包装下游观察者，并关联onNext,.....  放入主线程中执行
    static final class ObserverOnObserver<T> implements Observer<T>, Disposeble {
        final Observer<? super T> actual;
        Disposeble disposeble;
        private Handler handler;

        ObserverOnObserver(Observer<? super T> actual) {
            this.actual = actual;
            handler = new Handler(Looper.getMainLooper());
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
            actual.onSubscribe(d);
        }

        @Override
        public void onNext(final T t) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    actual.onNext(t);
                }
            });
        }

        @Override
        public void onError(final Throwable e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    actual.onError(e);
                }
            });
        }

        @Override
        public void onComplete() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    actual.onComplete();
                }
            });
        }
    }
}
