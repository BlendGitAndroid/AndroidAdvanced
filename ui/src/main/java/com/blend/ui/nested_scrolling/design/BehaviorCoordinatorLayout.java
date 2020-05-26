package com.blend.ui.nested_scrolling.design;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.blend.ui.R;

import java.lang.reflect.Constructor;


public class BehaviorCoordinatorLayout extends RelativeLayout implements NestedScrollingParent, ViewTreeObserver.OnGlobalLayoutListener {

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.getBehavior() != null) {
                params.getBehavior().onSizeChanged(this, child, w, h, oldw, oldh);
            }
        }
    }

    /**
     * 设置监听时一定要注意
     * 必须当前绘制完成onFinishInflate设置监听
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 反射实例化
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * 布局发生改变的时候
     */
    @Override
    public void onGlobalLayout() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (layoutParams.getBehavior() != null) {
                layoutParams.getBehavior().onLayoutFinish(this, child);
            }
        }
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
