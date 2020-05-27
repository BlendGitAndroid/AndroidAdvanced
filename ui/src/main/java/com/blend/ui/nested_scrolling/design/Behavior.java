package com.blend.ui.nested_scrolling.design;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Behavior作为一个中间者，目的是协调 子控件之间的触摸事件
 */
public class Behavior {

    public Behavior(Context context, AttributeSet set) {

    }

    public void onSizeChanged(View parent, View child, int w, int h, int oldW, int oldH) {

    }

    public void onLayoutFinish(View parent, View child) {

    }

    public void onTouchMove(View parent, View child, MotionEvent event, float x, float y, float oldX, float oldY) {

    }

    public void onNestedScroll(View scrollView, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }
}
