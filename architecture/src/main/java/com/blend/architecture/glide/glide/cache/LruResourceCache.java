package com.blend.architecture.glide.glide.cache;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

import com.blend.architecture.glide.glide.recycle.Resource;

public class LruResourceCache extends LruCache<Key, Resource> implements MemoryCache {

    boolean isRemoved;
    private ResourceRemovedListener listener;

    public LruResourceCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected void entryRemoved(boolean evicted, Key key, Resource oldValue, Resource newValue) {
        if (listener != null && oldValue != null && !isRemoved) {
            listener.onResourceRemoved(oldValue);
        }
    }

    @Override
    public Resource remove2(Key key) {
        //主动remove的不回调
        isRemoved = true;
        Resource remove = remove(key);
        isRemoved = false;
        return remove;
    }

    @Override
    protected int sizeOf(Key key, Resource value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return value.getBitmap().getAllocationByteCount();
        } else {
            return value.getBitmap().getByteCount();
        }
    }


    @Override
    public void setResourceRemovedListener(@NonNull ResourceRemovedListener listener) {
        this.listener = listener;
    }

    @Override
    public void clearMemory() {
        evictAll();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            trimToSize(maxSize() / 2);
        }
    }
}
