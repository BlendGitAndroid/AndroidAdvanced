package com.blend.architecture.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blend.architecture.R;
import com.blend.architecture.rxjava.rxjava.Disposeble;
import com.blend.architecture.rxjava.rxjava.Function;
import com.blend.architecture.rxjava.rxjava.Observable;
import com.blend.architecture.rxjava.rxjava.ObservableEmitter;
import com.blend.architecture.rxjava.rxjava.ObservableOnSubscribe;
import com.blend.architecture.rxjava.rxjava.Observer;

import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 设计模式：装饰模式，观察者模式，责任链模式，简单工厂模式
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
 * RxJava中还有Consumer和Action，也可以创建观察者。他们都是为了触发回调的，Consumer自带一个参数，Action不带餐宿。
 * 当被观察者发射 onNext时，由于onNext带有参数，所以使用Consumer；
 * 当被观察者发送onComplete时，由于onComplete不带参数，所以使用Action。
 * subscribe的几个重载方法：
 * 1)subscribe()不带任何参数，也就是说观察者没有任何回调。
 * 2)subscribe(Observer<? super T> observer)将Observer作为参数，它有四个回调方法。
 * 3)subscribe(Consumer<? super T> onNext)将Consumer作为参数，Consumer中有个回调方法accept，accept带有一个参数，接受被
 * 观察者发射过来的数据，当被观察者发射onNext时，accept将被执行。
 * 4)subscribe(Consume<? super T> onNext, Consumer<? super Throwable> onError)带有两个Consumer参数，分别负责onNext
 * 和onError的回到。
 * 5)subscribe(Consume<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete)带有三个参数，分别
 * 负责onNext、onError和onComplete的回调。
 * <p>
 *
 * <p>
 * 线程调度：简化了异步操作
 * subscribeOn：指定Observable这个被观察者自身在哪个调度器上执行，通过接收一个Scheduler参数，来指定对数据的处理运行在特定的线程调度器Scheduler
 * 上，若多次设定，则只有第一次起作用，和调用的位置没有关系。
 * observeOn：指定一个观察者在哪个调度器上观察这个Observable，接收一个Scheduler参数，来指定下游操作运行在特定的线程调度器Scheduler上，
 * 若多次设定，每次均起作用，在这里可以进行线程的切换。
 * 这两个操作符一般联合使用，第一步先使用subscribeOn指定这一行上面的代码执行线程，之后通过observeOn来切换线程。
 * 注意：若有多个subscribeOn，只有第一个subscribeOn指定的线程生效；若有observeOn指定的线程，则observeOn之后进行线程切换，不管subscribeOn指定的
 * 线程，即使subscribeOn是observeOn之后的第一个线程。
 * 原因是：RxJava代码的执行是先从上到下，依次添加被观察者，当执行到subscribe注册观察者的时候，再反向执行注册包装的观察者，此时subscribeOn执行，所以
 * subscribeOn是从下到上执行的，这也是为什么第一个subscribeOn指定的线程才生效；当执行到代码最顶部的时候，先进行onSubscribe回调，这也是为什么
 * onSubscribe执行在代码运行的线程；然后，代码再从上到下执行，若subscribeOn有指定线程，则之后的代码都是运行在subscribeOn指定的线程中，直到遇到
 * observeOn进行线程切换，若有多个observeOn，则线程进行多次切换。
 * <p>
 *
 * <p>
 * 背压：当上下游在不同的线程中，通过Observable发射，处理，响应数据流时，如果上游发射数据的速度快于下游接收处理数据的速度，这样对于那些没来得及处理的数
 * 据就会造成积压，这些数据既不会丢失，也不会被垃圾回收机制回收，而是存放在一个异步缓存池中，如果缓存池中的数据一直得不到处理，越积越多，最后就会造成内存
 * 溢出，这便是响应式编程中的背压（BackPressure）问题。
 * 解决思路：响应式拉取，响应式拉取是观察者主动去被观察者那里拉取事件，而被观察者则是被动等待通知再发射事件。
 * 策略：默认的缓冲池大小为BUFFER_SIZE = 128
 * MISSING：在此策略下，通过Create方法创建的Flowable相当于没有指定背压策略，不会对通过onNext发射的数据做缓存或丢弃处理，当缓存池满了，提示缓存区满，
 * 并抛出MissingBackpressureException异常。
 * <p>
 * ERROR：在此策略下，如果放入Flowable的异步缓存池中的数据超限了，则会抛出MissingBackpressureException异常。
 * <p>
 * BUFFER：内部维护了一个缓存池SpscLinkedArrayQueue，其大小不限，此策略下，如果Flowable默认的异步缓存池满了，会通过此缓存池暂存数据，
 * 它与Observable的异步缓存池一样，可以无限制向里添加数据，不会抛出MissingBackpressureException异常，但会导致OOM。超过observeOn配
 * 置的bufferSize则缓存到上游的缓冲队列，等待下游消耗了容量的3/4的事件之后，再继续发送上游缓存的事件给下游。
 * <p>
 * DROP：在此策略下，如果Flowable的异步缓存池满了，会丢掉上游发送的数据。
 * <p>
 * LATEST：与Drop策略一样，如果缓存池满了，会丢掉将要放入缓存池中的数据，不同的是，不管缓存池的状态如何，LATEST都会将最后一条数据强行放入缓存池中，
 * 来保证观察者在接收到完成通知之前，能够接收到Flowable最新发射的一条数据。当缓存队列中的事件消耗了缓存总容量的3/4之后，会再去接受上游的发送事件；
 * 如果在执行完等待队列3/4的事件之后，上游的事件还没发送结束，下游即会再次缓存上游发送过来的容量的3/4个事件。
 * 最后一个事件是怎么发出的？上游发送的事件，每次都会存贮在AtomicReference变量中，下游再去上游请求事件时，如果上游的事件已经发送结束，则将最新的
 * 这个值返回给下游。
 */
public class RxJavaMainActivity extends AppCompatActivity {

    private static final String TAG = "RxJavaMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_main);

        create();

        just();

        flowable();

        // interval();

        customize();

        subscribeOnTest();

        doOnSubscribe();
    }

    /*
    Observable每发送onSubscribe()之前都会回调这个方法。
    RxJavaMainActivity: doOnSubscribe:RxCachedThreadScheduler-1
    RxJavaMainActivity: create:RxNewThreadScheduler-1
    RxJavaMainActivity: map:RxCachedThreadScheduler-2
    RxJavaMainActivity: subscribe:main
    出现上面这种日志的情况是：从下到上，第一个subscribeOn之后由于线程切换，又会在onSubscribe之前执行doOnSubscribe，所以doOnSubscribe
    运行在这个subscribeOn的线程
     */
    private void doOnSubscribe() {
        io.reactivex.Observable<Integer> observable = io.reactivex.Observable.create(new io.reactivex.ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(io.reactivex.ObservableEmitter<Integer> observableEmitter) throws Exception {
                Log.i(TAG, "create:" + Thread.currentThread().getName());
                observableEmitter.onNext(1);
                observableEmitter.onComplete();
            }
        });
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .map(new io.reactivex.functions.Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "map:" + Thread.currentThread().getName());
                        return integer;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Log.i(TAG, "doOnSubscribe:" + Thread.currentThread().getName());
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "subscribe:" + Thread.currentThread().getName());
                    }
                });
    }


    /*
    onSubscribe: main
    map-1:RxNewThreadScheduler-1
    map-2:RxNewThreadScheduler-1
    map-3:RxNewThreadScheduler-1
    onNext: RxNewThreadScheduler-1
     */
    private void subscribeOnTest() {
        io.reactivex.Observable.just(1)
                .map(new io.reactivex.functions.Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "map-1:" + Thread.currentThread().getName());
                        return integer;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .map(new io.reactivex.functions.Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "map-2:" + Thread.currentThread().getName());
                        return integer;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new io.reactivex.functions.Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        Log.i(TAG, "map-3:" + Thread.currentThread().getName());
                        return integer;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
                .subscribe(new FlowableSubscriber<Integer>() {
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

    // //每隔一段时间发送一个事件，这个事件从0开始，不断增加1
    // private void interval() {
    //     io.reactivex.Observable.interval(1, TimeUnit.SECONDS)
    //             .doOnDispose(new Action() {
    //                 @Override
    //                 public void run() throws Exception {
    //                     Log.e(TAG, "interval: 结束");
    //                 }
    //             }).compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))  //自定义操作符，加入自己的代码，这里添加生命周期
    //             .subscribe(new Consumer<Long>() {
    //                 @Override
    //                 public void accept(Long aLong) throws Exception {
    //                     Log.e(TAG, "interval: 开始");
    //                 }
    //             });
    // }


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