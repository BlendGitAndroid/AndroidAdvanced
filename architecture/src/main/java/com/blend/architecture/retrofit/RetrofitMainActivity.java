package com.blend.architecture.retrofit;

import android.os.Bundle;
import android.os.Environment;
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

public class RetrofitMainActivity extends AppCompatActivity {

    private static final String TAG = "RetrofitMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_main);

        // executeTest();
        // enqueueTest();

        // withRxJava();

        // postTest();

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

        RetrofitApi serviceApi = retrofit.create(RetrofitApi.class);
        final Call<RetrofitBean<List<RetrofitBean.DataBean>>> officialAccounts = serviceApi.getOfficialAccounts();
        new Thread() {
            @Override
            public void run() {
                try {
                    Response<RetrofitBean<List<RetrofitBean.DataBean>>> response = officialAccounts.clone().execute();
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