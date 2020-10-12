package com.blend.optimization.bitmapmanage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ImageResize {

    /**
     * 缩放bitmap
     *
     * @param context
     * @param id    图片内容
     * @param maxW  原始宽度
     * @param maxH  原始高度
     * @return      可复用的内存
     */
    public static Bitmap resizeBitmap(Context context, int id, int maxW, int maxH, boolean hasAlpha, Bitmap reusable) {
        Resources resources = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只解码出 outxxx参数 比如 宽、高
        options.inJustDecodeBounds = true;  //不被加载到内存中的前提下，获取Bitmap的原始宽高
        BitmapFactory.decodeResource(resources, id, options);
        //根据宽、高进行缩放
        int w = options.outWidth;
        int h = options.outHeight;
        //设置缩放系数
        options.inSampleSize = calcuteInSampleSize(w, h, maxW, maxH);
        if (!hasAlpha) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        options.inJustDecodeBounds = false;
        //设置成能复用
        options.inMutable = true;
        options.inBitmap = reusable;
        return BitmapFactory.decodeResource(resources, id, options);
    }

    /**
     * 计算缩放系数
     *
     * @param w
     * @param h
     * @param maxW
     * @param maxH
     * @return 缩放的系数
     */
    private static int calcuteInSampleSize(int w, int h, int maxW, int maxH) {
        int inSampleSize = 1;
        if (w > maxW && h > maxH) {
            inSampleSize = 2;
            //循环 使宽、高小于 最大的宽、高
            while (w / inSampleSize > maxW && h / inSampleSize > maxH) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
