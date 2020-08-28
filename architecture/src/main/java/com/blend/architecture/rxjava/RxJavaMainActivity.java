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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 设计模式：装饰着模式
 */
public class RxJavaMainActivity extends AppCompatActivity {

    private static final String TAG = "RxJavaMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_main);

        just();

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