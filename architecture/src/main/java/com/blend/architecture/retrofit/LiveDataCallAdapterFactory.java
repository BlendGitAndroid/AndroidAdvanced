package com.blend.architecture.retrofit;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    public static LiveDataCallAdapterFactory create() {
        return new LiveDataCallAdapterFactory();
    }

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Type responseType = null;
        if (getRawType(returnType) != LiveData.class) { //获取泛型原始参数
            throw new IllegalStateException("return type must be parameterized");
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType); //获取泛型参数
        Class<?> rawObservableType = getRawType(observableType);    //得到泛型参数的原始类型
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
        } else {
            responseType = observableType;
        }

        return new LiveDataCallAdapter<Object>(responseType);
    }

    private class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<R>> {

        private Type responseType;

        public LiveDataCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        /**
         * 返回此适配器将HTTP响应正文转换为Java时使用的值类型对象。
         * 比如LiveData<Repo>的相应类型是Repo，这个类型用于准备传递给adapt的LiveData
         */
        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public LiveData<R> adapt(Call<R> call) {
            AtomicBoolean startedFlag = new AtomicBoolean(false);
            return new LiveData<R>() {  //自定义LiveData
                @Override
                protected void onActive() {
                    super.onActive();
                    if (startedFlag.compareAndSet(false, true)) {
                        call.enqueue(new Callback<R>() {
                            @Override
                            public void onResponse(Call<R> call, Response<R> response) {
                                postValue(response.body());
                            }

                            @Override
                            public void onFailure(Call<R> call, Throwable t) {
                                postValue(null);
                            }
                        });
                    }
                }
            };
        }
    }
}


