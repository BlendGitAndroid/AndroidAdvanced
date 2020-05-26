package com.blend.ui.recyclerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blend.ui.R;

public class ImplRecyclerViewRefreshStateCall implements PullRefreshRecyclerView.RecyclerViewRefreshStateCallBack {

    private static final String TAG = "ImplRecyclerViewRefresh";

    private View mHeaderView;
    private TextView mTextView;
    private ImageView mImageView;
    private ObjectAnimator mObjectAnimator;

    public ImplRecyclerViewRefreshStateCall(PullRefreshRecyclerView pullRefreshRecyclerView) {
        mHeaderView = pullRefreshRecyclerView.getRefreshHeaderView();
        mTextView = mHeaderView.findViewById(R.id.tv);
        mImageView = mHeaderView.findViewById(R.id.iv);
        mObjectAnimator = ObjectAnimator.ofFloat(mImageView, "rotation", 0, 360).setDuration(300);
        mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    /**
     * 下拉时执行缩放
     *
     * @param scrollY        下拉的距离
     * @param headViewHeight 头布局高度
     * @param deltaY         moveY-lastMoveY,正值为向下拉
     */
    @Override
    public void onPullDownRefreshState(int scrollY, int headViewHeight, int deltaY) {
        mTextView.setText("下拉刷新");
        float f = -(scrollY * 0.1f / headViewHeight);
        Log.e(TAG, "onPullDownRefreshState f: " + f);
        mTextView.setScaleX(f);
        mTextView.setScaleY(f);
    }

    /**
     * 当处于松手刷新时，头布局显示完全时显示效果
     *
     * @param scrollY 下拉的距离
     * @param deltaY  moveY-lastMoveY,正值为向下拉
     */
    @Override
    public void onReleaseRefreshState(int scrollY, int deltaY) {
        mTextView.setText("松手刷新");
    }

    /**
     * 正在刷新时
     */
    @Override
    public void onRefreshingState() {
        mTextView.setText("正在刷新");
        mTextView.setScaleX(1.0f);
        mTextView.setScaleY(1.0f);
        mObjectAnimator.start();
    }

    @Override
    public void onDefaultState() {
        if (mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
            mImageView.setRotation(0);
        }
    }
}
