package com.blend.architecture.rxjava.rxjava;

public class ObservableCreate<T> extends Observable<T> {
    final ObservableOnSubscribe<T> source;

    public ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        CreateEmitter<T> parent = new CreateEmitter<>(observer);
        observer.onSubscribe(parent);   //通知观察者订阅成功

        try {
            source.subscribe(parent);
        } catch (Exception e) {
            e.printStackTrace();
            parent.onError(e);
        }
    }

    static final class CreateEmitter<T> implements ObservableEmitter<T>, Disposeble {
        final Observer<? super T> observer;
        private boolean bool;

        public CreateEmitter(Observer<? super T> observer) {
            this.observer = observer;
        }

        @Override
        public void disposa(boolean bool) {
            this.bool = bool;
        }

        @Override
        public boolean isDisposad() {
            return bool;
        }

        @Override
        public void onNext(T value) {
            if(!bool){
                observer.onNext(value); //observer === MapObserver
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if(!bool) {
                observer.onError(throwable);
            }
        }

        @Override
        public void onComplete() {
            if(!bool) {
                observer.onComplete();
            }
        }
    }
}
