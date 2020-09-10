package com.blend.architecture.retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blend.architecture.R;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_main);

        // executeTest();
        //
        // enqueueTest();

        withRxJava();
    }

    //结合RxJava使用
    private void withRxJava() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        serviceApi.getOfficialAccountsObservable().subscribeOn(Schedulers.newThread()).subscribe(new Observer<RetrofitBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RetrofitBean retrofitBean) {
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
    private void enqueueTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        Call<RetrofitBean> officialAccounts = serviceApi.getOfficialAccounts();
        officialAccounts.clone().enqueue(new Callback<RetrofitBean>() {
            @Override
            public void onResponse(Call<RetrofitBean> call, Response<RetrofitBean> response) {
                for (int i = 0; i < response.body().getData().size(); i++) {
                    Log.i("retrofit", "enqueue: " + response.body().getData().get(i).toString());
                }
            }

            @Override
            public void onFailure(Call<RetrofitBean> call, Throwable t) {

            }
        });
    }

    //同步请求
    private void executeTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        final Call<RetrofitBean> officialAccounts = serviceApi.getOfficialAccounts();
        new Thread() {
            @Override
            public void run() {
                try {
                    Response<RetrofitBean> response = officialAccounts.clone().execute();
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