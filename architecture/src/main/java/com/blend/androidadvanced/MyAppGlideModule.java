package com.blend.androidadvanced;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.HttpUrlFetcher;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;

@GlideModule
@Excludes(value = {com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule.class})
public class MyAppGlideModule extends AppGlideModule {

    private static final String DISK_CACHE_DIR = "Glide_cache";
    private static final long DISK_CACHE_SIZE = 100 << 20; // 100M

    //配置图片缓存的路径和缓存空间大小
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, DISK_CACHE_DIR, DISK_CACHE_SIZE));
    }

    //注册指定类型的源数据，并指定它的图片加载所使用的ModelLoader
    //当图片开始加载的时候，会根据资源的类型，找到该图片的加载方式
    //使用replace来替换GlideUrl的加载方式
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        glide.getRegistry().append(CachedImage.class, InputStream.class, new ImageLoader.Factory());

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .eventListener(new EventListener() {
                    @Override
                    public void callStart(Call call) {
                        // 输出日志，用于确认使用了我们配置的 OkHttp 进行网络请求
                    }
                })
                .build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));

    }

    /**
     * 是否启用基于 Manifest 的 GlideModule，如果没有在 Manifest 中声明 GlideModule，可以通过返回 false 禁用
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    //自定义资源类型
    public static class CachedImage {

        private final String imageUrl;

        public CachedImage(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        /**
         * 原始的图片的 url，用来从网络中加载图片
         */
        public String getImageUrl() {
            return imageUrl;
        }

        /**
         * 提取时间戳之前的部分作为图片的 key，这个 key 将会被用作缓存的 key，并用来从缓存中找缓存数据
         */
        public String getImageId() {
            if (imageUrl.contains("?")) {
                return imageUrl.substring(0, imageUrl.lastIndexOf("?"));
            } else {
                return imageUrl;
            }
        }
    }

    //该资源的加载方式
    public static class ImageLoader implements ModelLoader<CachedImage, InputStream> {

        /**
         * 在这个方法中，我们使用 ObjectKey 来设置图片的缓存的键
         */
        @Override
        public LoadData<InputStream> buildLoadData(CachedImage cachedImage, int width, int height, Options options) {
            return new LoadData<>(new ObjectKey(cachedImage.getImageId()),
                    new HttpUrlFetcher(new GlideUrl(cachedImage.getImageUrl()), 15000));
        }

        @Override
        public boolean handles(CachedImage cachedImage) {
            return true;
        }

        public static class Factory implements ModelLoaderFactory<CachedImage, InputStream> {

            @Override
            public ModelLoader<CachedImage, InputStream> build(MultiModelLoaderFactory multiFactory) {
                return new ImageLoader();
            }

            @Override
            public void teardown() { /* no op */ }
        }
    }


}
