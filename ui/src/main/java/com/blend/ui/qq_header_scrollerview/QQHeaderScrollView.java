package com.blend.ui.qq_header_scrollerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ListView;

import com.blend.ui.R;


public class QQHeaderScrollView extends ListView {

    private ImageView mImageView;
    private int mImageViewHeight;

    public void setZoomImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public QQHeaderScrollView(Context context) {
        super(context);
    }

    public QQHeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY;
        mImageView.requestLayout();
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        View header = (View) mImageView.getParent();
        //ListView会滑出去的高度
        int deltaY = header.getTop();
        if (mImageView.getHeight() > mImageViewHeight) {
            mImageView.getLayoutParams().height = mImageView.getHeight() + deltaY;
            header.layout(header.getLeft(), 0, header.getRight(), header.getHeight());
            mImageView.requestLayout();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    class ResetAnimation extends Animation {

        private int extraHeight;
        private int currentHeight;

        public ResetAnimation(int targetHeight) {
            currentHeight = targetHeight;
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
