package com.blend.ui.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * ObjectAnimator：是要接收一个对象，并给出要改变这个对象的哪一个属性，之后就是设置时间等。
 * 如果这个对象的属性set方法中有重绘调用，比如invalidate方法，那么动画就会自动执行。
 * 如果没有那么就得手动添加动画的listener方法，调用invalidate方法使View绘制。
 * 比如添加了一个对象中没有的属性，那么自然找不到这个对象的方法，这个时候的ObjectAnimator就和ValueAnimator一样了。
 * <p>
 * ValueAnimator：接收的是值的改变，本身并不会产生任何动画。要想产生动画效果，也得添加listener方法，在这个方法中产生
 * 调用属性值的改变并调用invalidate进行绘制。
 * <p>
 * 插值器：不管是View动画还是属性动画都有插值器，就是根据时间流逝的百分比计算出当前动画要执行的位置。比如加减速插值器等。
 * <p>
 * 估值器：个人感觉在View动画中，估值器就是那四个动画的实现中applyTransformation()方法中实现的，因为只有这四种操作，所以Android
 * 自己设置了。
 * 但是在属性动画中，属性的变化时多种多样的，所以得叫我们自己设置估值器。Android自己有FloatEvaluator，IntEvaluator等。
 * evaluate方法中的fraction参数，就是插值器的值。而fraction方法的返回值就能从listener方法中得到，只有就能在listener中
 * 实现自己想要实现的效果了。
 */
public class MyAnimatorView extends View {

    public static final float RADIUS = 50f;

    private Point currentPoint;

    private Paint mPaint;

    private String color;

    public MyAnimatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (currentPoint == null) {
            currentPoint = new Point(RADIUS, RADIUS);
            drawCircle(canvas);
            startAnimation();
        } else {
            drawCircle(canvas);
        }
    }

    private void drawCircle(Canvas canvas) {
        float x = currentPoint.getX();
        float y = currentPoint.getY();
        canvas.drawCircle(x, y, RADIUS, mPaint);
    }

    private void startAnimation() {
        Point startPoint = new Point(RADIUS, RADIUS);
        Point endPoint = new Point(getWidth() - RADIUS, getHeight() - RADIUS);
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator anim2 = ObjectAnimator.ofObject(this, "color", new ColorEvaluator(),
                "#0000FF", "#FF0000");

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim).with(anim2);
        animSet.setDuration(5000);
        animSet.start();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        mPaint.setColor(Color.parseColor(color));
        invalidate();
    }

}
