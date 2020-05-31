package com.blend.ui.custom_recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.blend.ui.R;

import java.util.ArrayList;
import java.util.List;


public class CustomRecyclerView extends ViewGroup {

    private Adapter mAdapter;

    //y偏移量，内容偏移量
    private int mScrollY;

    //当前显示的view
    private List<View> mViewList;

    //当前滑动的Y值
    private int mCurrentY;

    //行数
    private int mRowCount;

    private boolean isNeedRelayout;

    //当前Recycler的宽度
    private int mWidth;

    //当前Recycler的高度
    private int mHeight;

    private int[] mHeights;

    //回收池
    private Recycler mRecycler;

    //view的第几行，占内容的第几行
    private int mFirstRow;

    //最小的滑动距离
    private int mTouchSlop;

    private int mMaxVelocity;

    private int mMinVelocity;

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            mRecycler = new Recycler(mAdapter.getViewTypeCount());
        }
        mScrollY = 0;
        mFirstRow = 0;
        isNeedRelayout = true;
        requestLayout();
    }

    public CustomRecyclerView(Context context) {
        this(context, null);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mViewList = new ArrayList<>();
        isNeedRelayout = true;
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaxVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (mAdapter != null) {
            mRowCount = mAdapter.getCount();
            mHeights = new int[mRowCount];
            for (int i = 0; i < mHeights.length; i++) {
                mHeights[i] = mAdapter.getHeight(i);
            }
        }
        int tmpH = sumArray(mHeights, 0, mHeights.length);
        int h = Math.min(heightSize, tmpH);
        setMeasuredDimension(widthSize, h);
    }

    private int sumArray(int[] array, int firstIndex, int count) {
        int sum = 0;
        count += firstIndex;
        for (int i = firstIndex; i < count; i++) {
            sum += array[i];
        }
        return sum;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isNeedRelayout || changed) {
            isNeedRelayout = false;
            mViewList.clear();
            removeAllViews();
            if (mAdapter != null) {
                //摆放子控件，先获取整个RecyclerView的宽高
                mWidth = r - l;
                mHeight = b - t;
                int left, top, right, bottom;
                //第一行不是从0开始
                top = -mScrollY;
                //摆放屏幕内能看到的子Item
                for (int i = 0; i < mRowCount && top < mHeight; i++) {
                    bottom = top + mHeights[i];
                    View view = makeAndStep(i, 0, top, mWidth, bottom);
                    mViewList.add(view);
                    top = bottom;
                }
            }
        }
    }

    //实例化一个有宽度，高度的View
    private View makeAndStep(int row, int left, int top, int right, int bottom) {
        View view = obtainView(row, right - left, bottom - top);
        view.layout(left, top, right, bottom);
        return view;
    }

    private View obtainView(int row, int width, int height) {
        //获取类型
        int itemType = mAdapter.getItemViewType(row);
        //根据类型获取Item
        View item = mRecycler.getRecyclerView(itemType);
        View view = mAdapter.getView(row, item, this);
        if (view == null) {
            throw new RuntimeException("convertView 不能为空");
        }
        view.setTag(R.id.tag_recycler_type_view, itemType);
        view.setTag(R.id.tag_recycler_row, row);
        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        addView(view, 0);
        return view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int y2 = Math.abs(mCurrentY - (int) ev.getRawY());
                if (y2 > mTouchSlop) {
                    intercept = true;
                }
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //y方向上移动的距离
                int y2 = (int) event.getRawY();
                int diffY = mCurrentY - y2;
                scrollBy(0, diffY);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void scrollBy(int x, int y) {
        mScrollY += y;
        mScrollY = scrollBounds(mScrollY, mFirstRow, mHeights, mHeight);
        if (mScrollY > 0) { //往上滑
            while (mHeights[mFirstRow] < mScrollY) {
                if (!mViewList.isEmpty()) {
                    removeTop();
                }
                mScrollY -= mHeights[mFirstRow];
                mFirstRow++;
            }

            while (getFilledHeight() < mHeight) {
                addBottom();
            }
        } else if (mScrollY < 0) {    //往下滑
            while (!mViewList.isEmpty() && getFilledHeight() - mHeights[mFirstRow + mViewList.size()] > mHeight) {
                removeBottom();
            }

            while (0 > mScrollY) {
                addTop();
                mFirstRow--;
                mScrollY += mHeights[mFirstRow + 1];
            }
        }

        repositionViews();

        //重绘
        awakenScrollBars();
    }

    //重新对子控件进行layout
    private void repositionViews() {
        int left, top, right, bottom, i;
        top = -mScrollY;
        i = mFirstRow;
        for (View view : mViewList) {
            bottom = top + mHeights[i++];
            view.layout(0, top, mWidth, bottom);
            top = bottom;
        }
    }

    private void addTop() {
        addTopAndBottom(mFirstRow - 1, 0);
    }

    private void addBottom() {
        int size = mViewList.size();
        addTopAndBottom(mFirstRow + size, size);
    }

    private void addTopAndBottom(int addRow, int index) {
        View view = obtainView(addRow, mWidth, mHeights[addRow]);
        mViewList.add(index, view);
    }

    private void removeTop() {
        removeView(mViewList.remove(0));
    }

    private void removeBottom() {
        removeView(mViewList.remove(mViewList.size() - 1));
    }

    private int getFilledHeight() {
        return sumArray(mHeights, mFirstRow, mViewList.size()) - mScrollY;
    }

    private int scrollBounds(int scrollY, int firstRow, int[] sizes, int viewSize) {
        if (scrollY > 0) {
            //往上滑
            scrollY = Math.min(scrollY, sumArray(sizes, firstRow, sizes.length - 1 - firstRow) - viewSize);
        } else {
            //往下滑
            scrollY = Math.max(scrollY, -sumArray(sizes, 0, firstRow));
        }
        return scrollY;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        int typeView = (int) view.getTag(R.id.tag_recycler_type_view);
        mRecycler.addRecyclerView(view, typeView);
    }
}
