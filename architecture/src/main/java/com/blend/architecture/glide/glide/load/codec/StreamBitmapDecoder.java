package com.blend.architecture.glide.glide.load.codec;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.blend.architecture.glide.glide.cache.ArrayPool;
import com.blend.architecture.glide.glide.recycle.BitmapPool;

import java.io.IOException;
import java.io.InputStream;

public class StreamBitmapDecoder implements ResourceDecoder<InputStream> {

    private final BitmapPool bitmaPool;
    private final ArrayPool arrayPool;

    public StreamBitmapDecoder(BitmapPool bitmapPool, ArrayPool arrayPool) {
        this.bitmaPool = bitmapPool;
        this.arrayPool = arrayPool;
    }

    @Override
    public boolean handles(InputStream source) throws IOException {
        return true;
    }

    @Override
    public Bitmap decode(InputStream source, int width, int height) throws IOException {
        MarkInputStream is;
        if (source instanceof MarkInputStream) {
            is = (MarkInputStream) source;
        } else {
            is = new MarkInputStream(source, arrayPool);
        }
        is.mark(0);
        //BitmapFactory所有方法都是用来解码创建一个Bitmap，
        // 其中有一个重要的类是Options，此类用于解码Bitmap时的各种参数控制
        BitmapFactory.Options options = new BitmapFactory.Options();
        //只加载大小
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        options.inJustDecodeBounds = false;
        //原宽高
        int sourceWidth = options.outWidth;
        int sourceHeight = options.outHeight;
        //目标宽高
        int targetWidth = width < 0 ? sourceWidth : width;
        int targetHeight = height < 0 ? sourceHeight : height;
        //获得缩放因子 缩放最大比例
        float widthFactor = targetWidth / (float) sourceWidth;
        float heightFactor = targetHeight / (float) sourceHeight;
        float factor = Math.max(widthFactor, heightFactor);
        //获得目标宽、高
        int outWidth = Math.round(factor * sourceWidth);
        int outHeight = Math.round(factor * sourceHeight);

        //分别获得宽、高需要缩放多大
        int widthScaleFactor = sourceWidth % outWidth == 0 ? sourceWidth / outWidth : sourceWidth
                / outWidth + 1;
        int heightScaleFactor = sourceHeight % outHeight == 0 ? sourceHeight / outHeight :
                sourceHeight / outHeight + 1;
        int sampleSize = Math.max(widthScaleFactor, heightScaleFactor);
        sampleSize = Math.max(1, sampleSize);

        options.inSampleSize = sampleSize;

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //复用
        //不管alpha  使用不同的策略
        Bitmap bitmap = bitmaPool.get(outWidth, outHeight, Bitmap.Config.RGB_565);
        options.inBitmap = bitmap;
        options.inMutable = true;
        is.reset();
        Bitmap result = BitmapFactory.decodeStream(is, null, options);
        is.release();
        return result;
    }


}
