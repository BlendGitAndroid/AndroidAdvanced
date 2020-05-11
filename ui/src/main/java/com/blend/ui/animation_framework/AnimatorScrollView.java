package com.blend.ui.animation_framework;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;


public class AnimatorScrollView extends ScrollView {

    private AnimatorLinearLayout mContent;

    public AnimatorScrollView(Context context) {
        super(context);
    }

    public AnimatorScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatorScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //渲染完毕之后
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContent = (AnimatorLinearLayout) getChildAt(0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        /**
         * 监听滑动程度，child从屏幕下面冒出多少距离，来计算出一个百分比来执行动画
         * 百分比（0~1之间） = 滑出高度 / child实际高度
         */

        //实际高度
        int scrollViewHeight = getHeight();

        for (int i = 0; i < mContent.getChildCount(); i++) {
            View child = mContent.getChildAt(i);
            int childHeight = child.getHeight();
            if (!(child instanceof DiscrollInterface)) {
                continue;
            }
            DiscrollInterface discrollInterface = (DiscrollInterface) child;

            int childTop = child.getTop();  //child离parent顶部的距离

            //child离屏幕顶部的距离          t:滑出屏幕的高度
            int absoluteTop = childTop - t;

            if (absoluteTop <= scrollViewHeight) {

                //child浮现的高度
                int visibleGap = scrollViewHeight - absoluteTop;

                //float ratio = child浮现的高度/child的高度
                float ratio = visibleGap / (float) childHeight;

                discrollInterface.onDiscroll(clamp(ratio, 1.0f, 0f));

            } else {
                discrollInterface.onRestDiscroll();
            }
        }
    }

    /**
     * 求中间值，保证在0~1之间
     */
    private float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, max), min);
    }
}
