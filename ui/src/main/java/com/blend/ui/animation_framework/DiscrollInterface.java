package com.blend.ui.animation_framework;

public interface DiscrollInterface {

    /**
     * 当滑动的时候，调用该方法，用于控制子控件执行相应的动画
     *
     * @param ratio 动画执行的百分比（child view画出来的距离百分比）
     */
    void onDiscroll(float ratio);

    /**
     * 重置动画，让View所有的属性都恢复到原来的样子
     */
    void onRestDiscroll();

}
