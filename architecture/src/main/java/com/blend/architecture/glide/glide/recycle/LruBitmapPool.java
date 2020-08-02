package com.blend.architecture.glide.glide.recycle;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.blend.architecture.glide.glide.recycle.BitmapPool;

public class LruBitmapPool extends LruCache<LruBitmapPool.Key, Bitmap> implements BitmapPool {


    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    public void put(Bitmap bitmap) {
        //必须要是易变的才能复用
        if (!bitmap.isMutable()) {
            bitmap.recycle();
            return;
        }
        //一个bitmap太大了 不复用了
        int size = getSize(bitmap);
        if (size >= maxSize()) {
            bitmap.recycle();
            return;
        }
        Key key = new Key(size, bitmap.getConfig());
        put(key, bitmap);
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        Key key = new Key(getSize(width, height, config), config);
        return remove(key);
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

    class Key {
        int size;
        Bitmap.Config config;

        public Key(int size, Bitmap.Config config) {
            this.size = size;
            this.config = config;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            Key key = (Key) object;

            if (size != key.size) return false;
            return config == key.config;
        }

        @Override
        public int hashCode() {
            int result = size;
            result = 31 * result + (config != null ? config.hashCode() : 0);
            return result;
        }
    }


    public int getSize(Bitmap value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return value.getAllocationByteCount();
        } else {
            return value.getByteCount();
        }
    }

    private int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        }
        return 2;
    }

    public int getSize(int width, int height, Bitmap.Config config) {
        return width * height * getBytesPerPixel(config);
    }

    @Override
    protected int sizeOf(Key key, Bitmap value) {
        return getSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, Key key, Bitmap oldValue, Bitmap newValue) {
        oldValue.recycle();
    }


}
