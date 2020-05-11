package com.blend.ui.animation_framework;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blend.ui.R;


public class AnimatorLinearLayout extends LinearLayout {

    public AnimatorLinearLayout(Context context) {
        super(context, null);
    }

    public AnimatorLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public AnimatorLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*考虑到系统控件不识别自定义属性，所以我们考虑给控件包一层帧，这里采用父容器组件给子容器包裹一层的方式
     * 系统通过加载布局文件，然后调用View的addView来进行加载，我们就能在这里偷天换日
     *
     * 在源码中，先调用generateLayoutParams组装XML属性参数，再调用addView进行添加，所以，自定义属性在
     * generateLayoutParams中进行组装添加，在addView中进行封装
     * */
    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        AnimatorLayoutParams layoutParams = (AnimatorLayoutParams) params;

        if (!isDiscrollable(layoutParams)) {
            super.addView(child, params);
        } else {
            AnimatorFrameLayout frameLayout = new AnimatorFrameLayout(child.getContext());

            frameLayout.addView(child);
            frameLayout.setDiscrollveAlpha(layoutParams.mDiscrollveAlpha);
            frameLayout.setDiscrollveFromBgColor(layoutParams.mDiscrollveFromBgColor);
            frameLayout.setDiscrollveToBgColor(layoutParams.mDiscrollveToBgColor);
            frameLayout.setDiscrollveScaleX(layoutParams.mDiscrollveScaleX);
            frameLayout.setDiscrollveScaleY(layoutParams.mDiscrollveScaleY);
            frameLayout.setDisCrollveTranslation(layoutParams.mDisCrollveTranslation);
            super.addView(frameLayout, layoutParams);
        }
    }

    private boolean isDiscrollable(AnimatorLayoutParams layoutParams) {
        return layoutParams.mDiscrollveAlpha || layoutParams.mDiscrollveScaleX || layoutParams.mDiscrollveScaleY
                || layoutParams.mDisCrollveTranslation != -1
                || (layoutParams.mDiscrollveFromBgColor != -1 && layoutParams.mDiscrollveToBgColor != -1);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AnimatorLayoutParams(getContext(), attrs);
    }

    public class AnimatorLayoutParams extends LinearLayout.LayoutParams {

        public boolean mDiscrollveAlpha;
        public boolean mDiscrollveScaleX;
        public boolean mDiscrollveScaleY;
        public int mDisCrollveTranslation;
        public int mDiscrollveFromBgColor;
        public int mDiscrollveToBgColor;


        public AnimatorLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            //没有属性传过来，给默认值
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DiscrollView_LayoutParams);
            mDiscrollveAlpha = a.getBoolean(R.styleable.DiscrollView_LayoutParams_discrollve_alpha, false);
            mDiscrollveScaleX = a.getBoolean(R.styleable.DiscrollView_LayoutParams_discrollve_scaleX, false);
            mDiscrollveScaleY = a.getBoolean(R.styleable.DiscrollView_LayoutParams_discrollve_scaleY, false);
            mDisCrollveTranslation = a.getInt(R.styleable.DiscrollView_LayoutParams_discrollve_translation, -1);
            mDiscrollveFromBgColor = a.getColor(R.styleable.DiscrollView_LayoutParams_discrollve_fromBgColor, -1);
            mDiscrollveToBgColor = a.getColor(R.styleable.DiscrollView_LayoutParams_discrollve_toBgColor, -1);
            a.recycle();
        }
    }
}
