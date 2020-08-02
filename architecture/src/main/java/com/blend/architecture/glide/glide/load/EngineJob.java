package com.blend.architecture.glide.glide.load;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.cache.Key;
import com.blend.architecture.glide.glide.recycle.Resource;
import com.blend.architecture.glide.glide.request.ResourceCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class EngineJob implements DecodeJob.Callback {

    private static final String TAG = "EngineJob";


    private static final Handler MAIN_THREAD_HANDLER =
            new Handler(Looper.getMainLooper(), new MainThreadCallback());

    private static final int MSG_COMPLETE = 1;
    private static final int MSG_EXCEPTION = 2;
    private static final int MSG_CANCELLED = 3;
    private Resource resource;


    /**
     * 任务工作回调
     */
    public interface EngineJobListener {

        void onEngineJobComplete(EngineJob engineJob, Key key, Resource resource);

        void onEngineJobCancelled(EngineJob engineJob, Key key);
    }

    private EngineKey key;
    private final List<ResourceCallback> cbs = new ArrayList<>();
    private final ThreadPoolExecutor executor;
    private final EngineJobListener listener;
    private boolean isCancelled;
    private DecodeJob decodeJob;


    public EngineJob(Context context, EngineKey key, EngineJobListener listener) {
        Glide glide = Glide.get(context);
        this.key = key;
        this.executor = glide.getExecutor();
        this.listener = listener;
    }

    public void addCallback(ResourceCallback cb) {
        Log.e(TAG, "设置加载状态监听");
        cbs.add(cb);
    }

    public void removeCallback(ResourceCallback cb) {
        Log.e(TAG, "移除加载状态监听");
        cbs.remove(cb);
        //这一个请求取消了，可能还有其他地方的请求
        //只有回调为空 才表示请求需要取消
        if (cbs.isEmpty()) {
            cancel();
        }
    }

    void cancel() {
        isCancelled = true;
        decodeJob.cancel();
        listener.onEngineJobCancelled(this, key);
    }

    public void start(DecodeJob decodeJob) {
        Log.e(TAG, "开始加载工作");
        this.decodeJob = decodeJob;
        executor.execute(decodeJob);
    }


    @Override
    public void onResourceReady(Resource resource) {
        this.resource = resource;
        MAIN_THREAD_HANDLER.obtainMessage(MSG_COMPLETE, this).sendToTarget();
    }

    @Override
    public void onLoadFailed(Throwable e) {
        MAIN_THREAD_HANDLER.obtainMessage(MSG_EXCEPTION, this).sendToTarget();
    }

    private static class MainThreadCallback implements Handler.Callback {


        @Override
        public boolean handleMessage(Message message) {
            EngineJob job = (EngineJob) message.obj;
            switch (message.what) {
                case MSG_COMPLETE:
                    job.handleResultOnMainThread();
                    break;
                case MSG_EXCEPTION:
                    job.handleExceptionOnMainThread();
                    break;
                case MSG_CANCELLED:
                    job.handleCancelledOnMainThread();
                    break;
                default:
                    throw new IllegalStateException("Unrecognized message: " + message.what);
            }
            return true;
        }
    }

    private void handleCancelledOnMainThread() {
        listener.onEngineJobCancelled(this, key);
        release();
    }

    private void handleResultOnMainThread() {
        if (isCancelled) {
            resource.recycle();
            release();
            return;
        }
        resource.acquire();
        listener.onEngineJobComplete(this, key, resource);
        for (int i = 0, size = cbs.size(); i < size; i++) {
            ResourceCallback cb = cbs.get(i);
            resource.acquire();
            cb.onResourceReady(resource);
        }
        resource.release();
        release();
    }

    private void handleExceptionOnMainThread() {
        if (isCancelled) {
            release();
            return;
        }
        listener.onEngineJobComplete(this, key, null);
        for (ResourceCallback cb : cbs) {
            cb.onResourceReady(null);
        }
    }

    private void release() {
        cbs.clear();
        key = null;
        resource = null;
        isCancelled = false;
        decodeJob = null;
    }

}
