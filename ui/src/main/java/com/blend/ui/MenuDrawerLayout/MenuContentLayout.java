package com.blend.ui.MenuDrawerLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.blend.ui.R;

/**
 * 完成布局摆放
 */
public class MenuContentLayout extends LinearLayout {

    private float mMaxTranslationX;
    private boolean opened = false;

    public MenuContentLayout(Context context) {
        this(context, null);
    }

    public MenuContentLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuContentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuDrawerTheme);
            mMaxTranslationX = a.getDimension(R.styleable.MenuDrawerTheme_maxTranslationX, 0);
            a.recycle();
        }
    }

    /**
     * 控制控件摆放位置
     * <p>
     * 遍历全部子控件，给每一个子控件进行偏移
     * 如果slideOffset = 1，侧滑菜单全部出来
     */
    public void setTouchY(float y, float slideOffset) {
        opened = slideOffset > 0.8;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setPressed(false);
            //判断y坐落在哪一个子控件，松手的那一刻，进行回调，跳转到其他页面
            boolean isHover = opened && y > child.getTop() && y < child.getBottom();
            if (isHover) {
                child.setPressed(true);
            }
            apply(child, y);
        }
    }

    private void apply(View child, float y) {
        //偏移距离
        float translationX = 0;
        //控件的中心点y坐标
        int centerY = child.getTop() + child.getHeight() / 2;
        //控制中心点，距离手指的距离
        float distance = Math.abs(y - centerY);
        //系数放大三倍
        float scale = distance / getHeight() * 3;
        translationX = mMaxTranslationX - scale * mMaxTranslationX;
        child.setTranslationX(translationX);
    }

    /**
     * 手指弹出操作
     */
    public void onMotionUp() {
        for (int i = 0; opened && i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.isPressed()) {
                //回调操作
                view.performClick();
            }
        }
    }


}
