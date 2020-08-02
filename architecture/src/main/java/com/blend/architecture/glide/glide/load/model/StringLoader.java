package com.blend.architecture.glide.glide.load.model;

import android.net.Uri;

import com.blend.architecture.glide.glide.load.model.ModelLoader;
import com.blend.architecture.glide.glide.load.model.ModelLoaderRegistry;

import java.io.File;
import java.io.InputStream;

public class StringLoader<Data> implements ModelLoader<String, Data> {

    /**
     * 代理
     */
    private final ModelLoader<Uri, Data> uriLoader;

    public StringLoader(ModelLoader<Uri, Data> uriLoader) {
        this.uriLoader = uriLoader;
    }

    @Override
    public LoadData<Data> buildLoadData(String model) {
        Uri uri;
        if (model.startsWith("/")) {
            uri = Uri.fromFile(new File(model));
        } else {
            uri = Uri.parse(model);
        }
        return uriLoader.buildLoadData(uri);
    }

    @Override
    public boolean handles(String s) {
        return true;
    }


    public static class StreamFactory implements ModelLoaderFactory<String, InputStream> {

        /**
         * 将String 交给 Uri 的组件处理
         *
         * @param modelLoaderRegistry
         * @return
         */
        @Override
        public ModelLoader<String, InputStream> build(ModelLoaderRegistry modelLoaderRegistry) {
            return new StringLoader<>(modelLoaderRegistry.build(Uri.class, InputStream.class));
        }
    }

}
