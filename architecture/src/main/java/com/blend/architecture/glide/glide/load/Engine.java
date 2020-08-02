package com.blend.architecture.glide.glide.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.cache.ActiveResources;
import com.blend.architecture.glide.glide.cache.Key;
import com.blend.architecture.glide.glide.cache.MemoryCache;
import com.blend.architecture.glide.glide.recycle.Resource;
import com.blend.architecture.glide.glide.request.ResourceCallback;

import java.util.HashMap;
import java.util.Map;


public class Engine implements MemoryCache.ResourceRemovedListener, Resource.ResourceListener,
        EngineJob.EngineJobListener {
    private static final String TAG = "Engine";

    public static class LoadStatus {
        private final EngineJob engineJob;
        private final ResourceCallback cb;

        LoadStatus(ResourceCallback cb, EngineJob engineJob) {
            this.cb = cb;
            this.engineJob = engineJob;
        }

        public void cancel() {
            engineJob.removeCallback(cb);
        }
    }

    private Context context;
    ActiveResources activeResources;
    Map<Key, EngineJob> jobs = new HashMap<>();

    public Engine(Context context) {
        this.context = context;
        activeResources = new ActiveResources(this);
    }

    public LoadStatus load(Object model, int width, int height, ResourceCallback cb) {
        EngineKey engineKey = new EngineKey(model, width, height);

        Resource resource = activeResources.get(engineKey);
        if (null != resource) {
            Log.e(TAG, "使用活跃缓存数据:"+resource);
            //引用数+1
            resource.acquire();
            cb.onResourceReady(resource);
            return null;
        }
        //从缓存移除 将它加入到活跃缓冲中
        resource = Glide.get(context).getMemoryCache().remove2(engineKey);
        if (null != resource) {
            Log.e(TAG, "使用内存缓存数据");
            // 加入正在使用集合 引用数+1
            activeResources.activate(engineKey, resource);
            resource.acquire();
            resource.setResourceListener(engineKey, this);
            cb.onResourceReady(resource);
            return null;
        }

        // 重复的请求 获得上一次的工作 并添加监听器
        // 请求完成 回调所有监听器
        EngineJob engineJob = jobs.get(engineKey);
        if (engineJob != null) {
            Log.e(TAG, "数据正在加载,添加数据加载状态监听");
            engineJob.addCallback(cb);
            return new LoadStatus(cb, engineJob);
        }
        // 创建一个新的加载任务
        engineJob = new EngineJob(context, engineKey, this);
        engineJob.addCallback(cb);
        DecodeJob decodeJob = new DecodeJob(context, model, engineKey, width, height, engineJob);
        engineJob.start(decodeJob);
        jobs.put(engineKey, engineJob);
        return new LoadStatus(cb, engineJob);
    }

    /**
     * 从内存缓存中移除回调
     * 将其加入复用池
     *
     * @param resource
     */
    @Override
    public void onResourceRemoved(Resource resource) {
        Log.e(TAG, "内存缓存移除，加入复用池");
        Glide.get(context).getBitmapPool().put(resource.getBitmap());
    }

    /**
     * 引用计数为0回调
     * 将其从正在使用集合移除 并加入内存缓存
     *
     * @param key
     * @param resource
     */
    @Override
    public void onResourceReleased(Key key, Resource resource) {
        Log.e(TAG,"引用计数为0,移除活跃缓存，加入内存缓存:"+key);
        activeResources.deactivate(key);
        Glide.get(context).getMemoryCache().put(key, resource);
    }

    @Override
    public void onEngineJobComplete(EngineJob engineJob, Key key, Resource resource) {
        if (resource != null) {
            resource.setResourceListener(key, this);
            activeResources.activate(key, resource);
        }
        jobs.remove(key);
    }

    @Override
    public void onEngineJobCancelled(EngineJob engineJob, Key key) {
        jobs.remove(key);
    }
}
