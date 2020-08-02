package com.blend.architecture.glide.glide.load.generator;

import android.util.Log;


import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.load.model.ModelLoader;
import com.blend.architecture.glide.glide.load.model.data.DataFetcher;

import java.util.List;

public class SourceGenerator implements DataGenerator, DataFetcher.DataFetcherCallback<Object> {
    private static final String TAG = "SourceGenerator";

    private final Glide glide;
    private final Object model;
    private final DataGeneratorCallback cb;
    private int loadDataListIndex;
    private List<ModelLoader.LoadData<?>> loadDatas;
    private ModelLoader.LoadData<?> loadData;

    public SourceGenerator(Glide glide, Object model, DataGeneratorCallback cb) {
        this.glide = glide;
        this.model = model;
        this.cb = cb;
        loadDatas = glide.getRegistry().getLoadDatas(model);
    }

    @Override
    public boolean startNext() {
        Log.e(TAG, "源加载器开始加载");
        boolean started = false;
        while (!started && hasNextModelLoader()) {
            loadData = loadDatas.get(loadDataListIndex++);
            Log.e(TAG, "获得加载设置数据");
            if (loadData != null && glide.getRegistry().hasLoadPath(loadData.fetcher.getDataClass
                    ())) {
                Log.e(TAG, "加载设置数据输出数据对应能够查找有效的解码器路径,开始加载数据");
                started = true;
                loadData.fetcher.loadData(this);
            }
        }
        return started;
    }


    private boolean hasNextModelLoader() {
        return loadDataListIndex < loadDatas.size();
    }

    @Override
    public void cancel() {
        if (loadData != null) {
            loadData.fetcher.cancel();
        }
    }

    @Override
    public void onFetcherReady(Object data) {
        Log.e(TAG, "加载器加载数据成功回调");
        cb.onDataReady(loadData.sourceKey, data, DataGeneratorCallback.DataSource.REMOTE);
    }

    @Override
    public void onLoadFailed(Exception e) {
        Log.e(TAG, "加载器加载数据失败回调");
        cb.onDataFetcherFailed(loadData.sourceKey, e);
    }
}
