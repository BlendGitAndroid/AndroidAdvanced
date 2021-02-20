package com.blend.ui.paint_gradient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.blend.ui.R;

class TelescopeView extends View {

    //放大镜的半径
    private static final int RADIUS = 300;
    // 原图
    private Bitmap mBitmap;

    // 制作的圆形的图片（放大的局部），盖在Canvas上面
    private ShapeDrawable mShapeDrawable;

    private Matrix mMatrix;

    private Paint rawPaint;

    public TelescopeView(Context context) {
        super(context);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xyjy3);

        //Bitmap着色器
        BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        //构建一个圆形的Shape，设置形状
        mShapeDrawable = new ShapeDrawable(new OvalShape());

        //给Shape设置着色器，设置着色器
        mShapeDrawable.getPaint().setShader(bitmapShader);

        // 切出矩形区域，用来画圆（内切圆），设置宽高
        mShapeDrawable.setBounds(0, 0, RADIUS * 2, RADIUS * 2);

        mMatrix = new Matrix();

        rawPaint = new Paint();
        rawPaint.setColor(Color.BLACK);
        rawPaint.setAntiAlias(true);
        rawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public TelescopeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        //设置平移矩阵
        mMatrix.setTranslate(RADIUS - x, RADIUS - y);

        //给着色器设置矩阵变化
        mShapeDrawable.getPaint().getShader().setLocalMatrix(mMatrix);

        // 切出手势区域点位置的圆，以手势点为原型
        mShapeDrawable.setBounds(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS);
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1、画原图
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), rawPaint);

        // 2、画放大镜的图，给ShapeDrawable设置画布，表示要在什么上绘制
        mShapeDrawable.draw(canvas);
    }
}
