package com.blend.ui.animation_framework;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class AnimatorFrameLayout extends FrameLayout implements DiscrollInterface {

    /**
     * <attr name="discrollve_translation">
     * <flag name="fromTop" value="0x01" />
     * <flag name="fromBottom" value="0x02" />
     * <flag name="fromLeft" value="0x04" />
     * <flag name="fromRight" value="0x08" />
     * </attr>
     * 0000000001
     * 0000000010
     * 0000000100
     * 0000001000
     * top|left
     * 0000000001 top
     * 0000000100 left 或运算 |
     * 0000000101
     * 反过来就使用& 与运算
     */
    //保存和定义自定义属性
    private static final int TRANSLATION_TO_TOP = 0x01;
    private static final int TRANSLATION_TO_BOTTOM = 0x02;
    private static final int TRANSLATION_TO_LEFT = 0x04;
    private static final int TRANSLATION_TO_RIGHT = 0x08;

    //颜色估值器
    private static ArgbEvaluator sArgbEvaluator = new ArgbEvaluator();

    //自定义属性接受的变量
    private int mDiscrollveFromBgColor;//背景颜色变化开始值
    private int mDiscrollveToBgColor;//背景颜色变化结束值
    private boolean mDiscrollveAlpha;//是否需要透明度动画
    private int mDisCrollveTranslation;//平移值
    private boolean mDiscrollveScaleX;//是否需要x轴方向缩放
    private boolean mDiscrollveScaleY;//是否需要y轴方向缩放
    private int mHeight;//本view的高度
    private int mWidth;//本view的宽度

    public AnimatorFrameLayout(@NonNull Context context) {
        super(context, null);
    }

    public AnimatorFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public AnimatorFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public static void setsArgbEvaluator(ArgbEvaluator sArgbEvaluator) {
        AnimatorFrameLayout.sArgbEvaluator = sArgbEvaluator;
    }

    public void setDiscrollveFromBgColor(int discrollveFromBgColor) {
        mDiscrollveFromBgColor = discrollveFromBgColor;
    }

    public void setDiscrollveToBgColor(int discrollveToBgColor) {
        mDiscrollveToBgColor = discrollveToBgColor;
    }

    public void setDiscrollveAlpha(boolean discrollveAlpha) {
        mDiscrollveAlpha = discrollveAlpha;
    }

    public void setDisCrollveTranslation(int disCrollveTranslation) {
        mDisCrollveTranslation = disCrollveTranslation;
    }

    public void setDiscrollveScaleX(boolean discrollveScaleX) {
        mDiscrollveScaleX = discrollveScaleX;
    }

    public void setDiscrollveScaleY(boolean discrollveScaleY) {
        mDiscrollveScaleY = discrollveScaleY;
    }

    //执行动画radio:0~1
    @Override
    public void onDiscroll(float ratio) {
        if (mDiscrollveAlpha) {
            setAlpha(ratio);
        }

        if (mDiscrollveScaleX) {
            setScaleX(ratio);
        }

        if (mDiscrollveScaleY) {
            setScaleY(ratio);
        }

        //平移动画  int值：left，right，top，bottom
        if (isTranslationTo(TRANSLATION_TO_BOTTOM)) {
            setTranslationY(mHeight * (1 - ratio)); //height--->0(0代表恢复到原来的位置)
        }

        if (isTranslationTo(TRANSLATION_TO_TOP)) {
            setTranslationX(-mHeight * (1 - ratio));
        }

        if (isTranslationTo(TRANSLATION_TO_LEFT)) {
            setTranslationX(-mWidth * (1 - ratio));
        }

        if (isTranslationTo(TRANSLATION_TO_RIGHT)) {
            setTranslationX(mWidth * (1 - ratio));
        }

        if (mDiscrollveFromBgColor != -1 && mDiscrollveToBgColor != -1) {
            setBackgroundColor((int) sArgbEvaluator.evaluate(ratio, mDiscrollveFromBgColor, mDiscrollveToBgColor));
        }
    }

    @Override
    public void onRestDiscroll() {

        if (mDiscrollveAlpha) {
            setAlpha(0);
        }

        if (mDiscrollveScaleX) {
            setScaleX(0);
        }

        if (mDiscrollveScaleY) {
            setScaleY(0);
        }

        if (isTranslationTo(TRANSLATION_TO_BOTTOM)) {
            setTranslationY(mHeight); //height--->0(0代表恢复到原来的位置)
        }

        if (isTranslationTo(TRANSLATION_TO_TOP)) {
            setTranslationX(-mHeight);
        }

        if (isTranslationTo(TRANSLATION_TO_LEFT)) {
            setTranslationX(-mWidth);
        }

        if (isTranslationTo(TRANSLATION_TO_RIGHT)) {
            setTranslationX(mWidth);
        }

    }

    private boolean isTranslationTo(int translationMask) {
        if (translationMask == -1) {
            return false;
        }
        //fromLeft|fromeBottom & fromBottom = fromBottom
        return (mDisCrollveTranslation & translationMask) == translationMask;
    }
}
