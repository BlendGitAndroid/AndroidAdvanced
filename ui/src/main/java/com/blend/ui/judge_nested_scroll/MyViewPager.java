package com.blend.ui.judge_nested_scroll;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

public class MyViewPager extends ViewPager {

    private static final String TAG = "MyViewPager";

    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // @Override
    // protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //     int height = 0;
    //     for (int i = 0; i < getChildCount(); i++) {
    //         View child = getChildAt(i);
    //         // child 的大小受自身的LayoutParams 和父view 的MeasureSpec 的双重限制！测量高度需要同时考虑这两个因素。
    //         ViewGroup.LayoutParams lp = child.getLayoutParams();
    //         child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.getMode(heightMeasureSpec)));
    //         int h = child.getMeasuredHeight();
    //         if (h > height) height = h;
    //     }
    //
    //     heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
    //     super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    // }


    @Override
    public Parcelable onSaveInstanceState() {
        Log.e(TAG, "onSaveInstanceState: ");
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Log.e(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(state);
    }
}
