package com.blend.architecture.rxjava;

import android.os.Bundle;
import android.util.Log;

import com.blend.architecture.R;
import com.blend.architecture.rxjava.rxjava.Disposeble;
import com.blend.architecture.rxjava.rxjava.Function;
import com.blend.architecture.rxjava.rxjava.Observable;
import com.blend.architecture.rxjava.rxjava.ObservableEmitter;
import com.blend.architecture.rxjava.rxjava.ObservableOnSubscribe;
import com.blend.architecture.rxjava.rxjava.Observer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 设计模式：装饰着模式，观察者模式
 * 叫你去搭建RxJava框架，你会怎么设计？
 * 三要素：观察者，被观察者，订阅(被观察者订阅观察者)
 *
 */
public class RxJavaMainActivity extends RxAppCompatActivity {

    private static final String TAG = "RxJavaMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_main);

        just();

        flowable();

        interval();

        customize();
    }

    private void just() {
        io.reactivex.Observable.just("BlendAndroid")   //添加被观察者
                .subscribeOn(Schedulers.io())   //添加被观察者   线程切换，runnable层层相调从主线程切换到io线程
                .observeOn(AndroidSchedulers.mainThread())  //添加被观察者    利用Handler切换回主线程
                .subscribe(new io.reactivex.Observer<String>() { //类似于接口回调，再反向注册成观察者
                    @Override
                    public void onSubscribe(Disposable d) {
                        //订阅观察者在哪个线程，这个方法就是在哪个线程，也就是当前线程
                        //因为在这个方法的整个回调过程中，没有涉及到线程的切换
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void flowable() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {

            }
        }, BackpressureStrategy.BUFFER) //默认
                .subscribeOn(Schedulers.newThread())    //发送数据和接收数据在不同的线程
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {   //这里的参数与之前的不同
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //每隔一段时间发送一个事件，这个事件从0开始，不断增加1
    private void interval() {
        io.reactivex.Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "interval: 结束");
                    }
                }).compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))  //自定义操作符，加入自己的代码，这里添加生命周期
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "interval: 开始");
                    }
                });
    }


    private void customize() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe 事件发射");
                emitter.onNext(1);
                emitter.onComplete();
            }
        }).map(new Function<Integer, String>() {

            @Override
            public String apply(Integer integer) {
                return integer + "blend";
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposeble d) {
                Log.e(TAG, "onSubscribe 成功");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onSubscribe===" + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });
    }
}