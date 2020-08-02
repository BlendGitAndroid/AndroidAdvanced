package com.blend.architecture.glide.glide.request;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.Target;
import com.blend.architecture.glide.glide.load.Engine;
import com.blend.architecture.glide.glide.recycle.Resource;


public final class Request implements Target.SizeReadyCallback, ResourceCallback {


    private enum Status {
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CANCELLED,
        CLEARED,
        PAUSED,
    }

    private Context context;
    private Object model;
    private RequestOptions requestOptions;
    private Target target;
    private Resource resource;
    private Engine.LoadStatus loadStatus;
    private Status status;
    private Drawable errorDrawable;
    private Drawable placeholderDrawable;

    public Request(Context context,
                   Object model,
                   RequestOptions requestOptions,
                   Target target) {
        this.context = context;
        this.model = model;
        this.requestOptions = requestOptions;
        this.target = target;
        status = Status.PENDING;

    }

    public void recycle() {
        context = null;
        model = null;
        requestOptions = null;
        target = null;
        loadStatus = null;
        errorDrawable = null;
        placeholderDrawable = null;
    }

    public void begin() {
        status = Status.WAITING_FOR_SIZE;
        //开始加载 先设置占位图片
        target.onLoadStarted(getPlaceholderDrawable());
        //宽高是否有效
        if (requestOptions.getOverrideWidth() > 0 && requestOptions.getOverrideHeight() > 0) {
            onSizeReady(requestOptions.getOverrideWidth(), requestOptions.getOverrideHeight());
        } else {
            //否则计算size 计算完成后会回调 onSizeReady
            target.getSize(this);
        }
    }

    /**
     * 取消
     */
    public void cancel() {
        target.cancel();
        status = Status.CANCELLED;
        if (loadStatus != null) {
            loadStatus.cancel();
            loadStatus = null;
        }
    }


    public void clear() {
        if (status == Status.CLEARED) {
            return;
        }
        cancel();
        // Resource must be released before canNotifyStatusChanged is called.
        if (resource != null) {
            releaseResource(resource);
        }
        status = Status.CLEARED;
    }

    public boolean isPaused() {
        return status == Status.PAUSED;
    }

    public void pause() {
        clear();
        status = Status.PAUSED;
    }

    private void releaseResource(Resource resource) {
        resource.release();
        this.resource = null;
    }

    public boolean isRunning() {
        return status == Status.RUNNING || status == Status.WAITING_FOR_SIZE;
    }

    public boolean isComplete() {
        return status == Status.COMPLETE;
    }

    public boolean isResourceSet() {
        return isComplete();
    }

    public boolean isCancelled() {
        return status == Status.CANCELLED || status == Status.CLEARED;
    }

    public boolean isFailed() {
        return status == Status.FAILED;
    }

    private Drawable getErrorDrawable() {
        if (errorDrawable == null && requestOptions.getErrorId() > 0) {
            errorDrawable = loadDrawable(requestOptions.getErrorId());
        }
        return errorDrawable;
    }

    private Drawable getPlaceholderDrawable() {
        if (placeholderDrawable == null && requestOptions.getPlaceholderId() > 0) {
            placeholderDrawable = loadDrawable(requestOptions.getPlaceholderId());
        }
        return placeholderDrawable;
    }


    private Drawable loadDrawable(int resourceId) {
        return ResourcesCompat.getDrawable(context.getResources(), resourceId, context.getTheme());
    }

    private void setErrorPlaceholder() {
        Drawable error = getErrorDrawable();
        if (error == null) {
            error = getPlaceholderDrawable();
        }
        target.onLoadFailed(error);
    }

    @Override
    public void onSizeReady(int width, int height) {
        if (status != Status.WAITING_FOR_SIZE) {
            return;
        }
        //运行状态
        status = Status.RUNNING;
        //加载图片
        loadStatus = Glide.get(context).getEngine().load(
                model,
                width,
                height,
                this);
    }

    @Override
    public void onResourceReady(Resource reference) {
        loadStatus = null;
        this.resource = reference;
        if (resource == null) {
            status = Status.FAILED;
            setErrorPlaceholder();
            return;
        }
        target.onResourceReady(resource.getBitmap());
    }


}
