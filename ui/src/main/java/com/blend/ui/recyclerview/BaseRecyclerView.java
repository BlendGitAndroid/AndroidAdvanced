package com.blend.ui.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

// 将点击坐标转化成表项索引
public class BaseRecyclerView extends RecyclerView {

    private GestureDetector mGestureDetector;

    public BaseRecyclerView(Context context) {
        super(context);
        init();
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {

            private static final int INVALID_POSITION = -1;
            private Rect mTouchFrame;

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //'获取单击坐标'
                int x = (int) e.getX();
                int y = (int) e.getY();
                //'获得单击坐标对应的表项索引'
                int position = pointToPosition(x, y);
                if (position != INVALID_POSITION) {
                    try {
                        //'获取索引位置的表项，通过接口传递出去'
                        View child = getChildAt(position);
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(child, getChildAdapterPosition(child), getAdapter());
                        }
                    } catch (Exception e1) {
                    }
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            //能拿到手指点击的x，y的位置，也能拿到每一个View的四个角的top，left，right,bottom，判断一下
            public int pointToPosition(int x, int y) {
                Rect frame = mTouchFrame;
                if (frame == null) {
                    mTouchFrame = new Rect();
                    frame = mTouchFrame;
                }

                final int count = getChildCount();
                for (int i = count - 1; i >= 0; i--) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == View.VISIBLE) {
                        child.getHitRect(frame);
                        if (frame.contains(x, y)) {
                            return i;
                        }
                    }
                }
                return INVALID_POSITION;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    //'将表项单击事件传递出去的接口'
    public interface OnItemClickListener {
        //'将表项view，表项适配器位置，适配器传递出去'
        void onItemClick(View item, int adapterPosition, Adapter adapter);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
