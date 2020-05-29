package com.blend.ui.MenuDrawerLayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 侧滑菜单，完成事件分发
 */
public class MenuDrawerLayout extends DrawerLayout implements DrawerLayout.DrawerListener {

    private MenuContentLayout mMenuContentLayout;
    private View mContentView;
    private MenuPutLayout mMenuPutLayout;
    private float mY;
    private float mSlideOffset;

    public MenuDrawerLayout(@NonNull Context context) {
        super(context);
    }

    public MenuDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof MenuContentLayout) {
                mMenuContentLayout = (MenuContentLayout) view;
            } else {
                mContentView = view;
            }
        }
        //先移除原来的View
        removeView(mMenuContentLayout);

        //再添加进来mMenuContentLayout
        mMenuPutLayout = new MenuPutLayout(mMenuContentLayout);

        addView(mMenuPutLayout);
        addDrawerListener(this);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mY = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            closeDrawers();
            mMenuContentLayout.onMotionUp();
            return super.dispatchTouchEvent(ev);
        }

        //在没有打开到设定值之前不拦截，
        if (mSlideOffset < 0.4) {
            return super.dispatchTouchEvent(ev);
        } else {
            mMenuPutLayout.setTouchY(mY, mSlideOffset);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        mSlideOffset = slideOffset;
        mMenuPutLayout.setTouchY(mY, mSlideOffset);
        //针对内容区域进行偏移
        float contentViewOffset = drawerView.getWidth() * slideOffset / 2;
        mContentView.setTranslationX(contentViewOffset);
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
