package com.blend.architecture.glide.glide.load.codec;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Bitmap解码器
 */
public interface ResourceDecoder<T> {
    boolean handles(T source) throws IOException;
    Bitmap decode(T source, int width, int height)throws IOException;
}
