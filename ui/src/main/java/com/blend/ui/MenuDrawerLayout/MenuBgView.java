package com.blend.ui.MenuDrawerLayout;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 完成背景绘制
 */
public class MenuBgView extends View {

    private Paint mPaint;
    private Path mPath;

    public MenuBgView(Context context) {
        this(context, null);
    }

    public MenuBgView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    public void setTouchY(float y, float percent) {
        mPath.reset();
        float width = getWidth() * percent;
        float height = getHeight();


        //超出部分
        float offsetY = getHeight() / 8;
        //开始点
        float beginX = 0;
        float beginY = -offsetY;
        //结束点
        float endX = 0;
        float endY = height + offsetY;
        //控制点
        float controlX = width * 3 / 2;
        float controlY = y;

        mPath.lineTo(beginX, beginY);
        mPath.quadTo(controlX, controlY, endX, endY);

        mPath.close();
        invalidate();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setColor(Drawable color) {
        if (color instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) color;
            mPaint.setColor(colorDrawable.getColor());
        } else {

        }
    }
}
