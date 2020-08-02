package com.blend.architecture.glide.glide.load.generator;

import android.util.Log;


import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.cache.Key;
import com.blend.architecture.glide.glide.load.model.ModelLoader;
import com.blend.architecture.glide.glide.load.model.data.DataFetcher;

import java.io.File;
import java.util.List;

public class DataCacheGenerator implements DataGenerator, DataFetcher.DataFetcherCallback<Object> {
    private static final String TAG = "DataCacheGenerator";

    private final DataGeneratorCallback cb;
    private final Object model;
    private final Glide glide;
    private List<ModelLoader<File, ?>> modelLoaders;
    private List<Key> keys;
    private int sourceIdIndex = -1;
    private Key sourceKey;
    private int modelLoaderIndex;
    private File cacheFile;
    private ModelLoader.LoadData<?> loadData;

    public DataCacheGenerator(Glide glide, Object model, DataGeneratorCallback cb) {
        this.glide = glide;
        this.model = model;
        this.cb = cb;
        //获得对应类型的所有key (当前只有ObjectKey用于磁盘缓存，磁盘缓存不需要宽、高等)
        //即如果文件缓存 获得缓存的key
        keys = glide.getRegistry().getKeys(model);
    }

    @Override
    public boolean startNext() {
        //modelLoaders为null 继续查找
        Log.e(TAG, "磁盘加载器开始加载");
        while (modelLoaders == null) {
            sourceIdIndex++;
            if (sourceIdIndex >= keys.size()) {
                return false;
            }
            Key sourceId = keys.get(sourceIdIndex);
            //获得磁盘缓存的文件
            cacheFile = glide.getDiskCache().get(sourceId);
            Log.e(TAG, "磁盘缓存存在则位于:" + cacheFile);
            if (cacheFile != null) {
                sourceKey = sourceId;
                Log.e(TAG, "获得所有文件加载器");
                //获得所有的文件加载器
                modelLoaders = glide.getRegistry().getModelLoaders(cacheFile);
                modelLoaderIndex = 0;
            }
        }

        boolean started = false;
        //找出好几个File为Model的 Loader 直到确定一个完全满足
        // 即 能够由此Loader解析完成
        while (!started && hasNextModelLoader()) {
            ModelLoader<File, ?> modelLoader = modelLoaders.get(modelLoaderIndex++);
            loadData = modelLoader.buildLoadData(cacheFile);
            Log.e(TAG, "获得加载设置数据");
            //是否可以把此loader加载的Data 解码出Bitmap
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
        return modelLoaderIndex < modelLoaders.size();
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
        cb.onDataReady(sourceKey, data, DataGeneratorCallback.DataSource.CACHE);
    }

    @Override
    public void onLoadFailed(Exception e) {
        Log.e(TAG, "加载器加载数据失败回调");
        cb.onDataFetcherFailed(sourceKey, e);
    }
}
