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
 * <p>
 *
 * <p>
 * 简介：一个可观测的序列来组成异步的、基于事件的程序的库。
 * 好处：RxJava其实就是提供一套异步编程的API，这套API是基于观察者模式的，而且是链式调用的，所以使用RxJava编写的代码的逻辑会非常简洁。
 * 使用场景：数据库的读写、大图片的载入、文件压缩/解压等各种需要放在后台工作的耗时操作
 * 三要素：被观察者(五种)，观察者，订阅。被观察者订阅观察者
 * 操作符：创建操作符、转换操作符、组合操作符、功能操作符、过滤操作符、条件操作符
 * <p>
 *
 * <p>
 * 线程调度：简化了异步操作
 * subscribeOn：指定Observable这个被观察者自身在哪个调度器上执行，通过接收一个Scheduler参数，来指定对数据的处理运行在特定的线程调度器Scheduler
 * 上，若多次设定，则只有第一次起作用，和调用的位置没有关系。
 * observeOn：指定一个观察者在哪个调度器上观察这个Observable，接收一个Scheduler参数，来指定下游操作运行在特定的线程调度器Scheduler上，
 * 若多次设定，每次均起作用，在这里可以进行线程的切换。
 * <p>
 *
 * <p>
 * 背压：当上下游在不同的线程中，通过Observable发射，处理，响应数据流时，如果上游发射数据的速度快于下游接收处理数据的速度，这样对于那些没来得及处理的数
 * 据就会造成积压，这些数据既不会丢失，也不会被垃圾回收机制回收，而是存放在一个异步缓存池中，如果缓存池中的数据一直得不到处理，越积越多，最后就会造成内存
 * 溢出，这便是响应式编程中的背压（BackPressure）问题。
 * 解决思路：响应式拉取，响应式拉取是观察者主动去被观察者那里拉取事件，而被观察者则是被动等待通知再发射事件。
 */
public class RxJavaMainActivity extends RxAppCompatActivity {

    private static final String TAG = "RxJavaMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_main);

        create();

        just();

        flowable();

        interval();

        customize();
    }

    private void create() {
        io.reactivex.Observable.create(new io.reactivex.ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(io.reactivex.ObservableEmitter<String> emitter) throws Exception {

            }
        }).subscribe(new io.reactivex.Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                runCustomize();
            }
        }).start();

    }

    private void runCustomize() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe 事件发射== " + Thread.currentThread().getName());
                emitter.onNext(1);
                emitter.onComplete();
            }
        }).map(new Function<Integer, String>() {

            @Override
            public String apply(Integer integer) {
                Log.e(TAG, "apply===" + Thread.currentThread().getName());
                return integer + "blend";
            }
        }).subscribeOn()    //在这里进行线程切换
                .observeOn()
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposeble d) {
                        Log.e(TAG, "onSubscribe 成功 ==" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onSubscribe===" + s + "==" + Thread.currentThread().getName());
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