package com.blend.architecture.glide.glide.recycle;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * 复用池
 */
public interface BitmapPool {


    void put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);


    void clearMemory();

    void trimMemory(int level);
}
