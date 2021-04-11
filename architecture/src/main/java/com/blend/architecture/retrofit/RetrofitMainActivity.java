package com.blend.architecture.retrofit;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blend.architecture.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * CallAdapterFactory：请求适配器工厂，使用工厂方法模式来创建对象，并使用适配器模式，将网络请求结果适配成需要的结果，比如
 * 默认的CallAdapterFactor的Call，Rxjava2的返回Observable，我们自定义的LiveData。在这个方法中开始网络的请求和将返回结果
 * 进行适配。
 * <p>
 * ConverterFactory：转换工厂，使用工厂方法模式来创建对象。用于将ResponseBody转换为type，和将type转换为requestBody。
 * <p>
 * 用到的设计模式：
 * 1.单例模式。饿汉式，获取Platform对象，是Android平台还是java8平台。
 * 2.Build模式。Retrofit对象的创建，ServiceMethod对象的创建。
 * 3.适配器模式。定义了适配和转换的接口。CallAdapter.Factory，比如将okhttp的请求，转换成自己定义的格式，使用Retrofit中的Call。
 * 4.工厂模式。定义了各种对象创建的接口，比如CallAdapterFactory，ConvertFactory。
 * 5.外观模式。在Retrofit接口中定义了很多网络请求方法，参数都在方法上进行定义，调用者只要传入参数，具体的逻辑不需要知道。对外提供了统一调度，
 * 屏蔽了内部实现。
 * 6.代理模式(静态代理和动态代理)。静态代理在DefaultCallAdapterFactory中，将okHttp对象进行代理。动态代理就是create方法中。
 * 7.装饰模式。将一个现有的对象添加新的功能，但是不改变其结果。DefaultCallAdapterFactory中的ExecutorCallbackCall类就是一个
 * 装饰模式，他装饰了OkHttpCall，添加新的功能时将返回结果切换到主线程去。
 * 感觉装饰模式里面就会用到代理模式。
 * <p>
 * 1.Retrofit如何将定义的interface转换成网络请求？
 * 通过动态代理。Retrofit的create方法添加网络接口类，生成具体的网络请求接口对象，然后通过这个对象在运行时通过动态代理InvocationHandler的
 * invoke方法统一处理具体的网络方法。invoke方法通过构造一个ServiceMethod对象，并调用他的invoke方法，通过设置的CallAdapter对象的adapter
 * 方法完成网络请求。ServiceMethod是对Host,URL,请求方法等封装，同时还存储了Okhttp对象的CallAdapter。
 * 2.CallAdapter机制如何实现？
 * 主要为了适配每一个网络请求方法返回不同的类型，Retrofit真正使用网络请求的是OkHttpCall这个类，这个类返回的是OkhttpCall的请求结果，但是怎么
 * 返回每一个网络请求方法不同的类型，比如Call，LiveData，Observer，这个时候就需要CallAdapter来进行适配转换。
 * 3.Convert机制如何实现？
 * 将网络请求数据进行转换，进行数据解析。
 */
public class RetrofitMainActivity extends AppCompatActivity {

    private static final String TAG = "RetrofitMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_main);

        executeTest();
        enqueueTest();

        enqueueLiveDataTest();

        withRxJava();

        postTest();

        loginTest();
    }

    private void loginTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        serviceApi.login("userName", "password").enqueue(new Callback<RetrofitBean<Boolean>>() {
            @Override
            public void onResponse(Call<RetrofitBean<Boolean>> call, Response<RetrofitBean<Boolean>> response) {
                Log.i(TAG, "onResponse: " + response.toString());
            }

            @Override
            public void onFailure(Call<RetrofitBean<Boolean>> call, Throwable t) {

            }
        });

    }

    private void postTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);

        //post上传单个图片
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/glide1.jpg");
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
        // serviceApi.upload1(requestBody).enqueue(new Callback<RetrofitBean<Boolean>>() {
        //     @Override
        //     public void onResponse(Call<RetrofitBean<Boolean>> call, Response<RetrofitBean<Boolean>> response) {
        //         Log.i(TAG, "onResponse: " + response.message());
        //     }
        //
        //     @Override
        //     public void onFailure(Call<RetrofitBean<Boolean>> call, Throwable t) {
        //
        //     }
        // });

        RequestBody userName = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), "username");
        RequestBody password = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), "password");
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", "fileName", requestBody);
        serviceApi.upload2(userName, password, part).enqueue(new Callback<RetrofitBean<Boolean>>() {
            @Override
            public void onResponse(Call<RetrofitBean<Boolean>> call, Response<RetrofitBean<Boolean>> response) {
                Log.i(TAG, "onResponse: " + response.message());
            }

            @Override
            public void onFailure(Call<RetrofitBean<Boolean>> call, Throwable t) {

            }
        });
    }

    //结合RxJava使用
    private void withRxJava() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        serviceApi.getOfficialAccountsObservable().subscribeOn(Schedulers.newThread()).subscribe(new Observer<RetrofitBean<List<RetrofitBean.DataBean>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RetrofitBean<List<RetrofitBean.DataBean>> retrofitBean) {
                for (int i = 0; i < retrofitBean.getData().size(); i++) {
                    Log.i("retrofit", "onNext: " + retrofitBean.getData().get(i).toString());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    //异步请求
    private void enqueueLiveDataTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MyGsonConvertFactory.create())
                .build();
        Class<RetrofitApi> retrofitApiClass = RetrofitApi.class;   //这个自动生成泛型的Class类对象
        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        LiveData<RetrofitBean<List<RetrofitBean.DataBean>>> officialAccounts = serviceApi.getLiveDataOfficialAccounts();
        officialAccounts.observe(this, new android.arch.lifecycle.Observer<RetrofitBean<List<RetrofitBean.DataBean>>>() {
            @Override
            public void onChanged(@Nullable RetrofitBean<List<RetrofitBean.DataBean>> listRetrofitBean) {
                if (listRetrofitBean != null) {
                    for (RetrofitBean.DataBean datum : listRetrofitBean.getData()) {
                        Log.i("retrofit", "enqueue: " + datum.toString());
                    }
                } else {
                    Log.i(TAG, "onChanged: no message");
                }
            }
        });
    }

    //异步请求
    private void enqueueTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        // Call<RetrofitBean> officialAccounts = serviceApi.getOfficialAccounts();
        Call<RetrofitBean<List<RetrofitBean.DataBean>>> officialAccounts = serviceApi.getProject2();
        officialAccounts.enqueue(new Callback<RetrofitBean<List<RetrofitBean.DataBean>>>() {
            @Override
            public void onResponse(Call<RetrofitBean<List<RetrofitBean.DataBean>>> call, Response<RetrofitBean<List<RetrofitBean.DataBean>>> response) {
                for (int i = 0; i < response.body().getData().size(); i++) {
                    Log.i("retrofit", "enqueue: " + response.body().getData().get(i).toString());
                }
            }

            @Override
            public void onFailure(Call<RetrofitBean<List<RetrofitBean.DataBean>>> call, Throwable t) {

            }
        });
    }

    //同步请求
    private void executeTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Class<RetrofitApi> retrofitApiClass = RetrofitApi.class;
        RetrofitApi serviceApi = retrofit.create(retrofitApiClass);
        final Call<RetrofitBean<List<RetrofitBean.DataBean>>> officialAccounts = serviceApi.getOfficialAccounts();
        new Thread() {
            @Override
            public void run() {
                try {
                    Response<RetrofitBean<List<RetrofitBean.DataBean>>> response = officialAccounts.execute();
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        Log.i("retrofit", "execute: " + response.body().getData().get(i).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}