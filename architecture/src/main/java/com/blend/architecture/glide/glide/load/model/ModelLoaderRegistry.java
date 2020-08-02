package com.blend.architecture.glide.glide.load.model;

import android.support.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;

public class ModelLoaderRegistry {
    private final List<Entry<?, ?>> entries = new ArrayList<>();

    public synchronized <Model, Data> void add(
            Class<Model> modelClass,
            Class<Data> dataClass,
            ModelLoader.ModelLoaderFactory<? extends Model, ? extends Data> factory) {
        Entry<Model, Data> entry = new Entry<>(modelClass, dataClass, factory);
        entries.add(entry);
    }


    public synchronized <Model, Data> ModelLoader<Model, Data> build(Class<Model> modelClass,
                                                                     Class<Data> dataClass) {
        List<ModelLoader<Model, Data>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            if (entry.handles(modelClass, dataClass)) {
                loaders.add((ModelLoader<Model, Data>) entry.factory.build(this));
            }
        }
        if (loaders.size() > 1) {
            return new MultiModelLoader<>(loaders);
        } else if (loaders.size() == 1) {
            return loaders.get(0);
        }
        throw new RuntimeException("No Have:" + modelClass.getName() + " Model Match " +
                dataClass.getName() + " Data");
    }


    /**
     * 获得符合model类型的loader集合
     *
     * @param modelClass
     * @param <Model>
     * @return
     */
    public <Model> List<ModelLoader<Model, ?>> getModelLoaders(Class<Model> modelClass) {
        List<ModelLoader<Model, ?>> modelLoaders = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            //model 符合的加入集合
            if (entry.handles(modelClass)) {
                modelLoaders.add((ModelLoader<Model, ?>) entry.factory.build(this));
            }
        }
        return modelLoaders;
    }


    private static class Entry<Model, Data> {
        private final Class<Model> modelClass;
        final Class<Data> dataClass;
        final ModelLoader.ModelLoaderFactory<? extends Model, ? extends Data> factory;

        public Entry(
                Class<Model> modelClass,
                Class<Data> dataClass,
                ModelLoader.ModelLoaderFactory<? extends Model, ? extends Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        public boolean handles(@NonNull Class<?> modelClass, @NonNull Class<?> dataClass) {
            return handles(modelClass) && this.dataClass.isAssignableFrom(dataClass);
        }

        public boolean handles(@NonNull Class<?> modelClass) {
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }

}
