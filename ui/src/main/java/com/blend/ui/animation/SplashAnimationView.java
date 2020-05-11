package com.blend.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.blend.ui.R;


public class SplashAnimationView extends View {


    private ValueAnimator mAnimator;

    //大圆（里面包含很多小圆）的半径
    private float mRotationRadius = 90;

    //每一个小圆的半径
    private float mCircleRadius = 18;

    //小圆的颜色列表
    private int[] mCircleColors;

    //大圆和小圆旋转的时间（ms）
    private long mRotationDuration = 1200;

    //第二部分动画的执行总时间（ms）
    private long mSplashDuration = 1200;

    //整体的背景颜色
    private int mSplashBgColor = Color.WHITE;

    //空心圆初始半径
    private float mHoleRadius = 0f;

    //当前大圆旋转角度（弧度）
    private float mCurrentRotationAngle = 0f;

    //当前大圆半径
    private float mCurrentRotationRadius = mRotationRadius;

    //绘制圆的画笔
    private Paint mPaint = new Paint();

    //绘制背景的画笔
    private Paint mPaintBg = new Paint();

    //屏幕正中心坐标
    private float mCenterX;
    private float mCenterY;

    //屏幕对角线一半长度
    private float mDiagonalDist;

    public SplashAnimationView(Context context) {
        super(context);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2f;
        mCenterY = h / 2f;
        mDiagonalDist = (float) Math.sqrt(w * w + h * h) / 2;   //勾股定理
    }

    private void init(Context context) {

        mCircleColors = context.getResources().getIntArray(R.array.splash_circle_colors);

        mPaint.setAntiAlias(true);

        mPaintBg.setAntiAlias(true);
        mPaintBg.setStyle(Paint.Style.STROKE);
        mPaintBg.setColor(mSplashBgColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mState == null) {
            mState = new RotateState();
        }
        mState.drawState(canvas);
    }

    public void splashDisappear() {
        if (mState != null && mState instanceof RotateState) {
            RotateState rotateState = (RotateState) mState;
            rotateState.cancel();
            mState = new MergingState();

            /*//post()讲解：https://www.cnblogs.com/dasusu/p/8047172.html
            post(new Runnable() {
                @Override
                public void run() {
                    mState = new MergingState();
                }
            });*/
        }
    }

    private SplashState mState = null;

    //使用策略模式，完成三种动画的
    private abstract class SplashState {

        public abstract void drawState(Canvas canvas);

        public void cancel() {
            mAnimator.cancel();
        }
    }

    /**
     * 旋转动画
     * 控制各个小圆的坐标---控制小圆的角度变化
     */
    private class RotateState extends SplashState {

        //在1200ms内，旋转的角度从0~2π
        public RotateState() {
            mAnimator = ValueAnimator.ofFloat(0f, (float) (Math.PI * 2));
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration(mRotationDuration);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    //计算某个时刻当前的大圆旋转了多少角度
                    mCurrentRotationAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.start();
        }

        @Override
        public void drawState(Canvas canvas) {
            drawBg(canvas);
            drawCircles(canvas);
        }
    }

    /**
     * 聚合动画
     * 大圆的半径不断变化
     */
    private class MergingState extends SplashState {

        public MergingState() {
            mAnimator = ValueAnimator.ofFloat(mRotationRadius, 0);
            mAnimator.setDuration(mRotationDuration);
            mAnimator.setInterpolator(new OvershootInterpolator(10f));
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mState = new ExpandState();
                }
            });
            mAnimator.reverse();
        }

        @Override
        public void drawState(Canvas canvas) {

            drawBg(canvas);

            drawCircles(canvas);

        }
    }

    /**
     * 水波纹扩散动画
     * 画一个空心圆，让它的画笔的粗细变得很大
     */
    private class ExpandState extends SplashState {

        public ExpandState() {
            mAnimator = ValueAnimator.ofFloat(mCircleRadius, mDiagonalDist);
            mAnimator.setDuration(mRotationDuration);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHoleRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.start();
        }

        @Override
        public void drawState(Canvas canvas) {
            drawBg(canvas);
        }
    }

    private void drawCircles(Canvas canvas) {
        //每个小圆之间的间隔角度（2π/小圆的个数）
        float rotationAngle = (float) (Math.PI * 2 / mCircleColors.length);

        for (int i = 0; i < mCircleColors.length; i++) {
            /**
             * x = r*cos(a) +centerX
             * y=  r*sin(a) + centerY
             * 每个小圆i*间隔角度 + 旋转的角度 = 当前小圆的真实角度
             */
            double angle = i * rotationAngle + mCurrentRotationAngle;
            float cx = (float) (mCurrentRotationRadius * Math.cos(angle) + mCenterX);
            float cy = (float) (mCurrentRotationRadius * Math.sin(angle) + mCenterY);
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(cx, cy, mCircleRadius, mPaint);
        }
    }

    private void drawBg(Canvas canvas) {
        if (mHoleRadius > 0) {
            //得到画笔的宽度 = 对角线/2 - 空心圆的半径
            float strokeWidth = mDiagonalDist - mHoleRadius;
            mPaintBg.setStrokeWidth(strokeWidth);
            //画圆的半径 = 空心圆的半径 + 画笔的宽度/2
            float radius = mHoleRadius + strokeWidth / 2;
            canvas.drawCircle(mCenterX, mCenterY, radius, mPaintBg);
        } else {
            canvas.drawColor(mSplashBgColor);
        }
    }

}
