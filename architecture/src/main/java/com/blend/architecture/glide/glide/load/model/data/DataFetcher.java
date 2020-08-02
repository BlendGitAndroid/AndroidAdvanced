package com.blend.architecture.glide.glide.load.model.data;

/**
 * 数据获取器
 */
public interface DataFetcher<T> {


    interface DataFetcherCallback<T> {


        void onFetcherReady(T data);


        void onLoadFailed(Exception e);
    }

    void loadData(DataFetcherCallback<? super T> callback);

    void cancel();

    Class<T> getDataClass();

}

