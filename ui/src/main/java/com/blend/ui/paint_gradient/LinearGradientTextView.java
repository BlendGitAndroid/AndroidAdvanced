package com.blend.ui.paint_gradient;

import android.annotation.Nullable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

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
        float textWith = mPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float hegiht = fontMetrics.bottom - fontMetrics.top;
        float size = getTextSize();
        row = (int) (hegiht / size);


        // 3个文字的宽度
        int gradientSize = (int) (textWith / text.length() * 3);

        // 从左边-gradientSize开始，即左边距离文字gradientSize开始渐变
        mLinearGradient = new LinearGradient(-gradientSize, size * curRow, 0, size * curRow, new int[]{
                0x22ffffff, 0xffffffff, 0x22ffffff}, null, Shader.TileMode.CLAMP
        );

        mPaint.setShader(mLinearGradient);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTranslate += DELTAX;
        float textWidth = getPaint().measureText(getText().toString());
        //到底部进行返回
        if (mTranslate > textWidth + 1 || mTranslate < 1) {
            DELTAX = 0;
            curRow++;
            if (curRow > row) {
                curRow = 0;
            }
        }

        mMatrix = new Matrix();
        mMatrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(mMatrix);
        postInvalidateDelayed(50);

    }
}
