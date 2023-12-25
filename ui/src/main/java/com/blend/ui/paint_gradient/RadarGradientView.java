package com.blend.ui.paint_gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 绘制雷达，设置两个画笔。
 * 一个画笔绘制圆圈。
 * 一个画笔绘制扫描，扫描使用扫描渐变，加上matrix旋转效果，通过postDelayed，不断的调用invalidate进行绘制。
 */
public class RadarGradientView extends View {


    public RadarGradientView(Context context) {
        super(context);

        // 画圆用到的paint
        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.STROKE); // 描边
        mPaintCircle.setStrokeWidth(1); // 宽度
        mPaintCircle.setAlpha(100); // 透明度
        mPaintCircle.setAntiAlias(true); // 抗锯齿
        mPaintCircle.setColor(Color.parseColor("#B0C4DE")); // 设置颜色 亮钢兰色

        // 扫描用到的paint
        mPaintRadar = new Paint();
        mPaintRadar.setStyle(Paint.Style.FILL_AND_STROKE); // 填充
        mPaintRadar.setAntiAlias(true); // 抗锯齿


        post(run);
    }

    public RadarGradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RadarGradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private int mWidth, mHeight;

    //五个圆
    private float[] pots = {0.05f, 0.1f, 0.15f, 0.2f, 0.25f};

    private Shader scanShader; // 扫描渲染shader

    //矩阵变换
    private Matrix matrix = new Matrix(); // 旋转需要的矩阵
    private int scanSpeed = 5; // 扫描速度

    private Paint mPaintCircle; // 画圆用到的paint
    private Paint mPaintRadar; // 扫描用到的paint


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < pots.length; i++) { //画从里面到外面的5个圆
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * pots[i], mPaintCircle);
        }

        // 画布的旋转变换 需要调用save() 和 restore()
        canvas.save();

        //这种实现方法虽然也能实现旋转，但是是通过旋转坐标系来做的
        // canvas.concat(matrix);

        // 左上角的矩形
        canvas.drawRect(0, 0, 200, 200, mPaintCircle);

        // 画扫描的圆
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * pots[4], mPaintRadar);

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 取屏幕的宽高是为了把雷达放在屏幕的中间
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth, mHeight);

        //扫描圆设置扫描渐变
        scanShader = new SweepGradient(mWidth / 2, mHeight / 2,
                new int[]{Color.TRANSPARENT, Color.parseColor("#84B5CA")}, null);

        mPaintRadar.setShader(scanShader); // 给画笔设置着色器
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            // 旋转矩阵
            matrix.postRotate(scanSpeed, mWidth / 2, mHeight / 2); // 旋转矩阵

            //这种方式实现的旋转，是通过对着色器设置旋转矩阵
            scanShader.setLocalMatrix(matrix);

            invalidate(); // 通知view重绘，调用onDraw
            postDelayed(run, 500); // 调用自身 重复绘制
        }
    };

}
