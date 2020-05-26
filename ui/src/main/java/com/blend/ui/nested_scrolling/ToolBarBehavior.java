package com.blend.ui.nested_scrolling;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.blend.ui.nested_scrolling.design.Behavior;


public class ToolBarBehavior extends Behavior {

    private int mMaxHeight = 400;

    public ToolBarBehavior(Context context, AttributeSet set) {
        super(context, set);
    }

    /**
     * 进行透明度变换
     */
    @Override
    public void onNestedScroll(View scrollView, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(scrollView, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (scrollView.getScrollY() <= mMaxHeight) {
            //改变透明度
            target.setAlpha(scrollView.getScrollY() * 1.0f / mMaxHeight);
        } else if (scrollView.getScrollY() == 0) {
            target.setAlpha(0);
        }
    }
}
