package com.blend.ui.MenuDrawerLayout;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/**
 * 提取，组合，转移
 * 是一个中间工作者
 * 专门为了给内容提供一个背景
 */
public class MenuPutLayout extends RelativeLayout {

    private MenuContentLayout mMenuContentLayout;
    private MenuBgView mBgView;

    public MenuPutLayout(MenuContentLayout contentLayout) {
        super(contentLayout.getContext());
        init(contentLayout);
    }

    public MenuPutLayout(Context context) {
        super(context);
    }

    public MenuPutLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuPutLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(MenuContentLayout contentLayout) {
        mMenuContentLayout = contentLayout;
        //把content的宽高转移到外面RelativeLayout
        setLayoutParams(mMenuContentLayout.getLayoutParams());

        //背景先添加进去
        mBgView = new MenuBgView(getContext());
        addView(mBgView, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //把contentView的背景颜色取出来设置给BgView把contentView弄成透明
        mBgView.setColor(contentLayout.getBackground());
        contentLayout.setBackgroundColor(Color.TRANSPARENT);
        addView(contentLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /**
     * 传递偏移y
     *
     * @param y
     * @param slideOffset
     */
    public void setTouchY(float y, float slideOffset) {
        mBgView.setTouchY(y, slideOffset);
        mMenuContentLayout.setTouchY(y, slideOffset);
    }
}
