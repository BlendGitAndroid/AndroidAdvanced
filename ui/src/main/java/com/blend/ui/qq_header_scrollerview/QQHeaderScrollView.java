package com.blend.ui.qq_header_scrollerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ListView;

import com.blend.ui.R;

/**
 * 使用getLayoutParams来改变ImageView的高度,调用requestLayout()方法重新布局
 */
public class QQHeaderScrollView extends ListView {

    private static final String TAG = "QQHeaderScrollView";

    private ImageView mImageView;
    private int mImageViewHeight;

    public void setZoomImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public QQHeaderScrollView(Context context) {
        this(context, null);
    }

    public QQHeaderScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQHeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mImageViewHeight = context.getResources().getDimensionPixelSize(R.dimen.dp_160);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            ResetAnimation resetAnimation = new ResetAnimation(mImageViewHeight);
            resetAnimation.setInterpolator(new OvershootInterpolator());
            resetAnimation.setDuration(700);
            mImageView.startAnimation(resetAnimation);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 产生过度
     * <p>
     * 不管是下滑还是上滑，都是-
     * 下滑：deltaY是负值，-去负值就是加
     * 上滑：deltaY是正值，-去正值就是减
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        Log.e(TAG, "overScrollBy() called with: deltaY = [" + deltaY + "]" + " ,mImageView.getHeight() - deltaY: " + (mImageView.getHeight() - deltaY));
        mImageView.getLayoutParams().height = Math.max(mImageView.getHeight() - deltaY, 0);
        mImageView.requestLayout();
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    // 自定义动画
    class ResetAnimation extends Animation {

        private int extraHeight;
        private int currentHeight;

        public ResetAnimation(int targetHeight) {
            currentHeight = mImageView.getHeight();
            extraHeight = mImageView.getHeight() - targetHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mImageView.getLayoutParams().height = (int) (currentHeight - extraHeight * interpolatedTime);
            mImageView.requestLayout();
            super.applyTransformation(interpolatedTime, t);
        }
    }

}
