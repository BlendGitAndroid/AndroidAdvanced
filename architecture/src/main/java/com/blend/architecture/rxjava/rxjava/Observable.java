package com.blend.architecture.rxjava.rxjava;

//被观察者
public abstract class Observable<T> implements ObservableSource {

    //创造操作符
    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        return new ObservableCreate<T>(source);
    }


    @Override
    public void subscribe(Observer observer) {
        subscribeActual(observer);
    }

    protected abstract void subscribeActual(Observer<? super T> observer);

    public <R> Observable<R> map(Function<? super T, ? extends R> function) {
        //传入上一个被观察者
        return new ObservableMap(this, function);
    }
}
