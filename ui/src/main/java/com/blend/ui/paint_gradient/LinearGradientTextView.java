package com.blend.ui.paint_gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * 在这个例子中，因为继承的是AppCompatTextView，所以可以拿到TextView的画笔，那么给这个画笔设置上着色器，
 * TextView在绘制的时候，就能按照这个画笔的设置来绘制出效果。
 * 利用矩阵平移，给着色器设置上矩阵平移变化，然后在调用postInvalidateDelayed重新绘制一遍，这些连接起来，
 * 我们就看到了从左到右的效果。
 */
public class LinearGradientTextView extends AppCompatTextView {
    private TextPaint mPaint;


    //LinearGradient线性渲染，   X,Y,X1,Y1四个参数只定位效果，不定位位置
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;

    private float mTranslate;
    private float DELTAX = 20;

    public LinearGradientTextView(Context context) {
        super(context);
    }

    public LinearGradientTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    int row;
    int curRow = 1;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 拿到TextView的画笔
        mPaint = getPaint();
        String text = getText().toString();

        //返回TextView的宽度
        float textWith = mPaint.measureText(text);

        //返回TextView的高度
        // Paint.FontMetrics 是一个类，用于描述绘制文本时，字体的度量信息。
        // 它包含了以下几个属性：
        // - ascent: 字体基线之上至字符顶部的距离
        // - descent: 字体基线之下至字符底部的距离
        // - top: 字体基线之上至字符顶部的最大距离
        // - bottom: 字体基线之下至字符底部的最大距离
        // - leading: 两行文字之间的额外间距。
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();

        // 获取文字的高度
        float height = fontMetrics.bottom - fontMetrics.top;

        //返回字体的大小
        float size = getTextSize();

        //整体高度除以每个字体的大小为有多少行
        row = (int) (height / size);

        // 3个文字的宽度，设置渐变的宽，TextView的宽度除以text的个数为一个字的宽度
        int gradientSize = (int) (textWith / text.length() * 3);

        // 从左边-gradientSize开始，即左边距离文字gradientSize开始渐变
        // 0x22ffffff, 0xffffffff, 0x22ffffff这三个参数分别表示开始颜色，中间颜色，结束颜色
        mLinearGradient = new LinearGradient(-gradientSize, 0, 0, 0, new int[]{
                0x22ffffff, 0xffffffff, 0x22ffffff}, null, Shader.TileMode.CLAMP
        );

        //给画笔设置着色器
        mPaint.setShader(mLinearGradient);

        mMatrix = new Matrix();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTranslate += DELTAX;

        //返回TextView的总体宽度
        float textWidth = getPaint().measureText(getText().toString());
        //到底部进行返回
        if (mTranslate > textWidth + 1 || mTranslate < 1) {
            // DELTAX = 0;
            curRow++;
            mTranslate = 0;
            if (curRow > row) {
                curRow = 0;
            }
        }

        mMatrix.setTranslate(mTranslate, 0);

        //给线性渐变着色器设置本地矩阵，这个矩阵表示x向右平移
        mLinearGradient.setLocalMatrix(mMatrix);
        postInvalidateDelayed(100);
    }
}
