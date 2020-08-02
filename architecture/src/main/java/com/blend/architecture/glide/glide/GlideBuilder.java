package com.blend.architecture.glide.glide;

import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;


import com.blend.architecture.glide.glide.cache.ArrayPool;
import com.blend.architecture.glide.glide.cache.DiskCache;
import com.blend.architecture.glide.glide.cache.DiskLruCacheWrapper;
import com.blend.architecture.glide.glide.cache.LruArrayPool;
import com.blend.architecture.glide.glide.cache.LruResourceCache;
import com.blend.architecture.glide.glide.cache.MemoryCache;
import com.blend.architecture.glide.glide.load.Engine;
import com.blend.architecture.glide.glide.load.GlideExecutor;
import com.blend.architecture.glide.glide.manager.RequestManagerRetriever;
import com.blend.architecture.glide.glide.recycle.BitmapPool;
import com.blend.architecture.glide.glide.recycle.LruBitmapPool;
import com.blend.architecture.glide.glide.request.RequestOptions;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * A builder class for setting default structural classes for Glide to use.
 */
public final class GlideBuilder {
    Engine engine;
    BitmapPool bitmapPool;
    MemoryCache memoryCache;
    ThreadPoolExecutor executor;
    DiskCache diskCache;
    RequestOptions defaultRequestOptions = new RequestOptions();
    ArrayPool arrayPool;

    public GlideBuilder setBitmapPool(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
        return this;
    }


    public GlideBuilder setMemoryCache(MemoryCache memoryCache) {
        this.memoryCache = memoryCache;
        return this;
    }

    public GlideBuilder setArrayPool(ArrayPool arrayPool) {
        this.arrayPool = arrayPool;
        return this;
    }


    public GlideBuilder setDiskCache(DiskCache diskCache) {
        this.diskCache = diskCache;
        return this;
    }

    public GlideBuilder setDefaultRequestOptions(RequestOptions defaultRequestOptions) {
        this.defaultRequestOptions = defaultRequestOptions;
        return this;
    }

    public GlideBuilder setExecutor(ThreadPoolExecutor service) {
        this.executor = service;
        return this;
    }



    private static int getMaxSize(ActivityManager activityManager) {
        //使用最大可用内存的0.4作为缓存使用
        final int memoryClassBytes = activityManager.getMemoryClass() * 1024 * 1024;
        return Math.round(memoryClassBytes * 0.4f);
    }

    public Glide build(Context context) {
        context = context.getApplicationContext();
        if (executor == null) {
            executor = GlideExecutor.newExecutor();
        }

        if (arrayPool == null) {
            arrayPool = new LruArrayPool();
        }

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context
                .ACTIVITY_SERVICE);
        int maxSize = getMaxSize(activityManager);

        //减去数组缓存后的可用内存大小
        int availableSize = maxSize - arrayPool.getMaxSize();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        // 获得一个屏幕大小的argb所占的内存大小
        int screenSize = widthPixels * heightPixels * 4;

        //bitmap复用占 4份
        float bitmapPoolSize = screenSize * 4.0f;
        //内存缓存占 2份
        float memoryCacheSize = screenSize * 2.0f;

        if (bitmapPoolSize + memoryCacheSize <= availableSize) {
            bitmapPoolSize = Math.round(bitmapPoolSize);
            memoryCacheSize = Math.round(memoryCacheSize);
        } else {
            //把总内存分成 6分
            float part = availableSize / 6.0f;
            bitmapPoolSize = Math.round(part * 4);
            memoryCacheSize = Math.round(part * 2);
        }

        if (bitmapPool == null) {
            bitmapPool = new LruBitmapPool((int) bitmapPoolSize);
        }

        if (memoryCache == null) {
            memoryCache = new LruResourceCache((int) memoryCacheSize);
        }

        if (diskCache == null) {
            diskCache = new DiskLruCacheWrapper(context);
        }

        if (engine == null) {
            engine = new Engine(context);
        }
        memoryCache.setResourceRemovedListener(engine);

        RequestManagerRetriever requestManagerRetriever =
                new RequestManagerRetriever();

        return new Glide(context, requestManagerRetriever, this);
    }
}
