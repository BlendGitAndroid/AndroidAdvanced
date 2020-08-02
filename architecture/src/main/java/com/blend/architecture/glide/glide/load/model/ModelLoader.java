package com.blend.architecture.glide.glide.load.model;


import com.blend.architecture.glide.glide.cache.Key;
import com.blend.architecture.glide.glide.load.model.data.DataFetcher;

/**
 * 模型加载器
 */
public interface ModelLoader<Model, Data> {

    interface ModelLoaderFactory<Model, Data> {
        ModelLoader<Model, Data> build(ModelLoaderRegistry modelLoaderRegistry);
    }

    class LoadData<Data> {
        public final Key sourceKey;
        public final DataFetcher<Data> fetcher;

        public LoadData(Key sourceKey, DataFetcher<Data> fetcher) {
            this.sourceKey = sourceKey;
            this.fetcher = fetcher;
        }

    }

    LoadData<Data> buildLoadData(Model model);

    boolean handles(Model model);
}
