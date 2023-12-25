package com.blend.ui.paint_gradient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.MotionEvent;
import android.view.View;

import com.blend.ui.R;

public class ZoomImageView extends View {

    //放大倍数
    private static final int FACTOR = 2;
    //放大镜的半径
    private static final int RADIUS = 100;
    // 原图
    private Bitmap mBitmap;
    // 放大后的图
    private Bitmap mBitmapScale;
    // 制作的圆形的图片（放大的局部），盖在Canvas上面
    private ShapeDrawable mShapeDrawable;

    private Matrix mMatrix;

    public ZoomImageView(Context context) {
        super(context);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xyjy3);

        //新建一个放大后的整个图片
        mBitmapScale = Bitmap.createScaledBitmap(mBitmapScale, mBitmapScale.getWidth() * FACTOR,
                mBitmapScale.getHeight() * FACTOR, true);

        //Bitmap着色器
        BitmapShader bitmapShader = new BitmapShader(mBitmapScale, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        //构建一个圆形的Shape，设置形状
        mShapeDrawable = new ShapeDrawable(new OvalShape());

        //给Shape设置着色器，设置着色器
        mShapeDrawable.getPaint().setShader(bitmapShader);

        // 切出矩形区域，用来画圆（内切圆），设置宽高
        mShapeDrawable.setBounds(0, 0, RADIUS * 2, RADIUS * 2);

        mMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1、画原图
        canvas.drawBitmap(mBitmap, 0, 0, null);

        // 2、画放大镜的图，给ShapeDrawable设置画布，表示要在什么上绘制
        mShapeDrawable.draw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        //设置平移矩阵，为什么这里是向负方向移动，因为设置所有的操作都是在放大后的Bitmap上设置的
        mMatrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR);

        //给着色器设置矩阵变化
        mShapeDrawable.getPaint().getShader().setLocalMatrix(mMatrix);

        // 切出手势区域点位置的圆，以手势点为原型
        mShapeDrawable.setBounds(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS);
        invalidate();
        return true;
    }
}
