package com.blend.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.blend.ui.R;

/**
 * 属性动画可以对任意的对象进行动画而不是View，动画默认时间间隔300ms，默认帧率10ms/帧。
 * <p>
 * 其原理是：属性动画要求动画作用的对象提供该属性的set和get方法，属性对象根据外界传过来的该属性的初始值和最终值（通过反射的调用set），
 * 整个过程是通过Handler，以动画的效果多次调用set方法，每次传递给set方法的值都不一样，确切的说是随着时间的推移，所传递的值越来越接近最终值。
 * <p>
 * 属性动画注意内存泄漏。
 * <p>
 * 属性动画原理：当调用start方法后，之后会注册AnimationHandler回调，在这个回调中，是使用Choreographer机制，每隔10ms回调doFrame方法，
 * 在这个方法中，mAnimationCallbacks这个List如果大于0，就能继续接收回调，当动画执行完或者取消动画的时候，mAnimationCallbacks的大小
 * 就会等于0，就不会接收Choreographer回调了。
 * Choreographer回调中会返回当前时间，利用(当前时间 - 开始时间)/动画时长就能得到fraction，调用插值器计算出真正时间的百分比（也可以理解成）
 * 属性的百分比，然后PropertyValuesHolder根据这个百分比，然后调用估值器，最后的属性值就在估值器里面计算，估值器计算的参数就是keyFrame的
 * 值，指的是两个关键帧之间的估值。
 * 计算完之后回调AnimatorUpdateListener接口，在这个接口里就能拿到getAnimatedValue估值器结算完的值。
 * <p>
 * 属性动画每一个属性，都会对应一个PropertyValuesHolder对象，每一个PropertyValuesHolder至少有两个KeyFrame，这个KeyFrame就是时间百分比和
 * 属性值的一对一对应。
 * 在动画中设置的values值，最后都会对应上KeyFrame，有几个值，就对应几个KeyFrame。
 * <p>
 * 而在ObjectAnimator方法中，在initAnimation方法中会调用PropertyValuesHolder的setupSetterAndGetter方法，会点加上set和Get，并使用
 * 反射来调用。会在doFrame后的animateValue重写这个方法，在这个方法中使用反射来调用。
 *
 * <p>
 * <p>
 * AnimatorUpdateListener这个接口是特有的，就是用来取估值器的计算出来的值，这些值都是保存在PropertyValuesHolder中。
 * animateValue方法中先根据插值器计算出属性变化的百分比，然后再调用KeyFrame，根据估值器计算出属性变化的值。
 * 在ObjectAnimator中使用这个值反射调用方法，在ValueAnimator中则是使用AnimatorUpdateListener回调给调用者处理。
 * 在ObjectAnimator方法中反射调用方法会该表属性的值，但是若是在这个方法中没有动画变化的，还需要调用者自己处理。
 */
public class AnimationMainActivity extends AppCompatActivity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animtation_main);
        iv = findViewById(R.id.img_iv);
    }

    public void startAnimator(View view) {

        int id = view.getId();
        if (id == R.id.btn_object) {
            startObjectAnimatorAnim(iv);
        } else if (id == R.id.btn_animatorset) {
            startAnimatorSet(iv);
        } else if (id == R.id.btn_value) {
            startValueAnimatorAnim(view);
        } else if (id == R.id.btn_interpolator) {
            startInterpolatorApply(iv);
        } else if (id == R.id.btn_valueholder) {
            startPropertyValueHolderAnim(iv);
        } else if (id == R.id.btn_evaluator) {
            startEvaluator(iv);
        } else if (id == R.id.btn_splash) {
            startSplash();
        } else if (id == R.id.btn_layout) {
            startActivity(new Intent(this, LayoutTransitionActivity.class));
        } else if (id == R.id.btn_key_frame) {
            startKeyFrame(iv);
        }

    }

    /**
     * 一个PropertyValuesHolder可以对应多个KeyFrame，是一对多的关系。
     * 一个PropertyValuesHolder至少对应两个KeyFrame，一个是开始，一个是结束。
     * 使用KeyFrameSet来管理KeyFrame。
     * <p>
     * KeyFrame就是一个记录fraction和其相对应的value值。
     */
    private void startKeyFrame(View view) {
        Keyframe scale1 = Keyframe.ofFloat(0, 0);
        Keyframe scale2 = Keyframe.ofFloat(0.5f, 0.5f);
        Keyframe scale3 = Keyframe.ofFloat(1.0f, 1.0f);
        PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder.ofKeyframe("scale", scale1, scale2, scale3);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(propertyValuesHolder);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue("scale");
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });
        valueAnimator.start();

    }

    private void startSplash() {
        startActivity(new Intent(this, SplashActivity.class));
    }


    /**
     * 操作对象属性，不局限于对象
     * 完成透明动画
     *
     * @param v
     */
    public void startObjectAnimatorAnim(View v) {
//        200 1-0.3

        //1.设置参数
        //2.执行动画
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0.3f, 1.0f);
        alphaAnim.setDuration(1000);//执行时间
        alphaAnim.setStartDelay(300);//延迟
        alphaAnim.start();

    }


    /**
     * 在这里，改变的是Button的按钮大小，但是Button不能通过setWidth来改变按钮的大小，怎么办？
     * <p>
     * 采用ValueAnimator，监听动画过程，自己实现属性的改变。
     * ValueAnimator本身不作用于任何对象，直接使用没有效果，它可以对一个值做动画，可以监听其动画过程，在动画中修改我们的对象的
     * 属性值。
     *
     * @param v
     */
    public void startValueAnimatorAnim(final View v) {
        //组合使用300
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "hehe", 0f, 100f, 50f);

        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

//				animation.getAnimatedFraction();//百分比
                float value = (float) animation.getAnimatedValue();//百分比的所对应的值
                v.setScaleX(0.5f + value / 200);
                v.setScaleY(0.5f + value / 200);

            }
        });
        animator.start();
//		animator.setRepeatCount(2);//重复次数
//		animator.setRepeatCount(ValueAnimator.INFINITE);//无限次数


    }

    public void startPropertyValueHolderAnim(View v) {
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("alpha", 1f, 0.5f);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleX", 1f, 2f);
        PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.5f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, holder1, holder2, holder3);
        animator.setDuration(200);
        animator.start();

    }


    public void startAnimatorSet(View v) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv, "translationX", 0f, 500F);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv, "alpha", 0f, 1f);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(iv, "scaleX", 0f, 2f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);

        //animatorSet.play(animator3).with(animator2).after(animator1);//链式调用顺序
        //animatorSet.play(animator3).with(animator2).before(animator1);//animator1
        //animatorSet.playTogether(animator1,animator2,animator3);//一起执行
        animatorSet.playSequentially(animator1, animator2, animator3);//顺序执行
        animatorSet.start();
    }

    /**
     * 属性动画要求对象的属性有set方法和get方法（非必须）
     *
     * 比如一个匀速的动画：需要线性插值器和整形估值算法
     *
     * 自定义插值器需要实现Interpolator或者TimeInterpolator,自定义估值算法需要实现TypeEvaluator，若是对于其它类型
     * 需要自定义（非int，float，Color）做动画，需要自定义类型估值算法。
     */


    /**
     * 估值器(Evaluator)
     * 自由落体效果实现
     * 核心目的：自定义变换规则
     * <p>
     * 系统预设的有：
     * IntEvaluator(针对整形属性)
     * FloatEvaluator(针对浮点型属性)
     * ArgbEvaluator(针对Color属性)
     *
     * @param v
     */
    public void startEvaluator(final View v) {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(3000);
        animator.setObjectValues(new PointF(0, 0));
        final PointF point = new PointF();
        //估值
        animator.setEvaluator(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                point.x = 100f * (fraction * 5);
                // y=vt=1/2*g*t*t(重力计算)
                point.y = 0.5f * 130f * (fraction * 5) * (fraction * 5);


                return point;
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF p = (PointF) animation.getAnimatedValue();
                v.setX(p.x);
                v.setY(p.y);
            }
        });
        animator.start();
    }

    /**
     * 插值器(Interpolator)
     * 由API提供的一组算法，用来操作动画执行是的变换规则，省去了一些自己写算法的麻烦，大致分为九种
     *
     * @param v
     */
    public void startInterpolatorApply(final View v) {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(3000);
        animator.setObjectValues(new PointF(0, 0)); // 设置初始值为0,0
        final PointF point = new PointF();
        //估值
        animator.setEvaluator(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                point.x = 100f * (fraction * 5);
                // y=vt=1/2*g*t*t(重力计算)
                point.y = 0.5f * 98f * (fraction * 5) * (fraction * 5);
                return point;
            }
        });

        /**
         * AnimatorUpdateListener监听整个动画过程，动画由许多帧组成，每播放一帧，onAnimationUpdate就会被调用一次
         *
         * AnimatorListenerAdapter可以有选择性的实现方法，一般有开始，结束，取消以及重复播放的监听。
         */

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setX(point.x);
                v.setY(point.y);
            }
        });


        //详细算法见资料图片
//		加速插值器，公式： y=t^(2f) （加速度参数. f越大，起始速度越慢，但是速度越来越快）
        animator.setInterpolator(new AccelerateInterpolator(10));

//      减速插值器公式: y=1-(1-t)^(2f) （描述: 加速度参数. f越大，起始速度越快，但是速度越来越慢）
//		animator.setInterpolator(new DecelerateInterpolator());

//      先加速后减速插值器 y=cos((t+1)π)/2+0.5
//		animator.setInterpolator(new AccelerateDecelerateInterpolator());

//      张力值, 默认为2，T越大，初始的偏移越大，而且速度越快 公式：y=(T+1)×t3–T×t2
//		animator.setInterpolator(new AnticipateInterpolator());

//      张力值tension，默认为2，张力越大，起始和结束时的偏移越大，
//      而且速度越快;额外张力值extraTension，默认为1.5。公式中T的值为tension*extraTension
//		animator.setInterpolator(new AnticipateOvershootInterpolator());
//      弹跳插值器
        animator.setInterpolator(new BounceInterpolator());
//      周期插值器 y=sin(2π×C×t)  周期值，默认为1；2表示动画会执行两次
//		animator.setInterpolator(new CycleInterpolator(2));
//		线性插值器，匀速公式：Y=T
//		animator.setInterpolator(new LinearInterpolator());

//      公式: y=(T+1)x(t1)3+T×(t1)2 +1
//      描述: 张力值，默认为2，T越大，结束时的偏移越大，而且速度越快
//		animator.setInterpolator(new OvershootInterpolator());

        animator.start();

    }


}
