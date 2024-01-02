package com.blend.ui.nested_scrolling.design;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.blend.ui.R;

import java.lang.reflect.Constructor;


/**
 * 模仿实现CoordinatorLayout，实现两个功能
 * <p>
 * 1.作为顶层布局，协调子布局
 * 2.作为一个能响应特定的一个或多个子视图交互的容器。
 * <p>
 * <p>
 * 在Activity的onCreate()方法中调用setContentView方法:
 * Activity显示到界面时的View的回调:
 * Constructors -> onFinishInflate -> onAttachedToWindow -> onWindowVisibilityChanged:visible -> onMeasure ->onMeasure
 * -> onSizeChanged -> onLayout -> onDraw -> onWindowFocusChanged
 * <p>
 * 当退出当前Activity后，View的回调
 * onWindowVisibilityChanged：gone -> onWindowFocusChanged ->onDetachedFromWindow
 * <p>
 * <p>
 * <p>
 * 网址：https://www.jianshu.com/p/0a4cb44ce9d1
 * View结合Activity的启动过程：
 * Activity：调用onCreate方法，这个时候我们setContentView加载了带View的布局
 * Activity：调用onWindowAttributesChanged方法，而且这个方法连续调用多次
 * View：调用构造方法
 * View：调用onFinishInflate方法，说明这个时候View已经填充完毕，但是这个时候还没开始触发绘制过程
 * Activity：调用onStart方法
 * Activity：再次调用onWindowAttributesChanged方法，说明这个方法在onResume之前会多次调用
 * Activity：调用onResume，我们一般认为当Activity调用onResume的时候，整个Activity已经可以和用户进行交互了，但事实上可能并不是这样，onWindowFocusChanged回调且方法为true时，才是真正可以和用户交互的时机。
 * Activity：调用onAttachedToWindow，说明跟Window进行了绑定。发现了吗，Activity在onResume之后才跟Window进行了绑定。
 * View：调用onAttachedToWindow，View开始跟Window进行绑定，这个过程肯定是在Activity绑定之后才进行的。
 * View：调用onWindowVisibilityChanged(int visibility)，参数变为 View.VISIBLE，说明Window已经可见了，这个时候我们发现一个问题就是其实onResume的时候似乎并不代表Activity中的View已经可见了。
 * View：调用onMeasure，开始测量
 * View：调用onSizeChanged，表示测量完成，尺寸发生了变化
 * View：调用onLayout，开始摆放位置
 * View：调用onDraw，开始绘制
 * Activity：调用onWindowFocusChanged(boolean hasFocus)，此时为true，代表窗体已经获取了焦点
 * View：调用onWindowFocusChanged(boolean hasWindowFocus)，此时为true，代表当前的控件获取了Window焦点，当调用这个方法后说明当前Activity中的View才是真正的可见了。
 * <p>
 * View结合Activity的退出过程：
 * Activity：调用onPause
 * View：调用onWindowVisibilityChanged(int visibility)，参数变为 View.GONE，View中对应的Window隐藏
 * Activity：调用onWindowFocusChanged(boolean hasFocus)，此时为false，说明Activity所在的Window已经失去焦点
 * Activity：调用onStop，此时Activity已经切换到后台
 * Activity：调用onDestory，此时Activity开始准备销毁，实际上调用onDestory并不代表Activity已经销毁了
 * View：调用onDetachedFromWindow，此时View与Window解除绑定，在这里可以做一些资源的释放，防止内存泄露
 * Activity：调用onDetachedFromWindow，此时Activity与Window解除绑定
 *
 * 进入的方法: 先Activity再View, 退出的方法: 先View再Activity
 */

public class BehaviorCoordinatorLayout extends RelativeLayout implements NestedScrollingParent, ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "BehaviorCoordinatorLayo";

    private float mLastX;
    private float mLastY;

    public BehaviorCoordinatorLayout(Context context) {
        super(context);
    }

    public BehaviorCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BehaviorCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /*
    当尺寸发生变化的时候会调用,一般是第一次测量之后调用，后面再测量，如果尺寸没变化就不会再去调用了。
    一般只会调用一次
    这个时候绘制已经完成
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: " + w + " " + h + " " + oldw + " " + oldh);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.getBehavior() != null) {
                params.getBehavior().onSizeChanged(this, child, w, h, oldw, oldh);
            }
        }
    }

    /*
     * 一般是通过LayoutInflater进行填充的时候会走这个方法。如果我们是直接在代码中new出来的View进行添加，是不会走这个方法的
     * <p>
     * 设置监听时一定要注意
     * 必须当前绘制完成onFinishInflate设置监听
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e(TAG, "onFinishInflate: ");
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /*
    为true这个说明View所绑定的Window开始获取焦点,false没有焦点
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    /*
        View已经跟它对应的Window已经绑定了
         */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /*
    View.VISIBLE，代表View所在的Window已经可见了

    View.GONE,此时Window已经不可见了
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    /**
     * 反射实例化
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * 布局发生改变的时候,每次滑动都会调用
     */
    @Override
    public void onGlobalLayout() {
        Log.e(TAG, "onGlobalLayout: ");
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (layoutParams.getBehavior() != null) {
                layoutParams.getBehavior().onLayoutFinish(this, child);
            }
        }
    }

    /*
    当前View与它对应的Window解除绑定
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getRawX();
                mLastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    if (params.getBehavior() != null) {
                        params.getBehavior().onTouchMove(this, child, event, moveX, moveY, mLastX, mLastY);
                    }
                }
                mLastX = moveX;
                mLastY = moveY;
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 滚动事件
     * <p>
     * move 肯定是拿不到
     * <p>
     * 实现了NestedScrolling机制的滚动控件
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onStopNestedScroll(View child) {

    }

    /**
     * @param target
     * @param dxConsumed:表示target已经消费的x方向的距离
     * @param dyConsumed:表示target已经消费的y方向的距离
     * @param dxUnconsumed:表示x方向剩下的滑动距离
     * @param dyUnconsumed:表示y方向剩下的滑动距离
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.getBehavior() != null) {
                params.getBehavior().onNestedScroll(target, child, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            }
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {

    }

    /**
     * @param target
     * @param dx:表示target本次滚动产生的x方向的滚动总距离
     * @param dy:表示target本次滚动产生的y方向的滚动总距离
     * @param consumed:表示父布局要消费的滚动距离,consumed[0]和consumed[1]分别表示父布局在x和y方向上消费的距离
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

    }

    /**
     * 可以捕获对内部View的fling事件，如果return true则表示拦截掉内部View的事件
     *
     * @param target
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    // 自定义LayoutParams
    public static class LayoutParams extends RelativeLayout.LayoutParams {

        private Behavior mBehavior;

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.BehaviorCoordinatorLayout);
            mBehavior = parseBehavior(c, attrs, a.getString(R.styleable.BehaviorCoordinatorLayout_layout_behavior));
            a.recycle();
        }

        public Behavior getBehavior() {
            return mBehavior;
        }

        private Behavior parseBehavior(Context context, AttributeSet attrs, String name) {
            if (TextUtils.isEmpty(name)) {
                return null;
            }
            try {
                Class clazz = Class.forName(name, true, context.getClassLoader());
                Constructor c = clazz.getConstructor(new Class[]{Context.class, AttributeSet.class});
                c.setAccessible(true);
                return (Behavior) c.newInstance(context, attrs);
            } catch (Exception e) {
                throw new RuntimeException("Could not inflate Behavior sub class" + name, e);
            }
        }
    }
}
