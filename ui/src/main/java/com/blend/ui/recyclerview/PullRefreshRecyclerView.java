package com.blend.ui.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.blend.ui.R;


public class PullRefreshRecyclerView extends LinearLayout {

    private static final String TAG = "PullRefreshRecyclerView";

    private int mTouchSlop;
    private boolean flag = false;

    //分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    //分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerViewRefreshStateCallBack mRefreshStateCallBack;

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    //默认状态
    private static final int DEFAULT = 0;

    //头部显示不全
    private final static int PULL_DOWN_REFRESH = 1;

    //头部显示全
    private final static int RELEASE_REFRESH = 2;

    //刷新中
    private final static int REFRESHING = 3;

    //加载更多
    private final static int LOAD_MORE = 4;

    //状态标记
    private int state = DEFAULT;

    //刷新头部宽度
    private int refreshHeaderWidth;

    //刷新头部高度
    private int refreshHeaderHeight;

    private OnPullListener mOnPullListener;
    private View mRefreshHeaderView;

    private RecyclerView.LayoutManager mLayoutManager;

    int refreshHeaderViewId;

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mLayoutManager = manager;
        mRecyclerView.setLayoutManager(manager);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
    }

    public View getRefreshHeaderView() {
        return mRefreshHeaderView;
    }

    public void setRecyclerViewRefreshStateCallBack(RecyclerViewRefreshStateCallBack recyclerViewRefreshStateCallBack) {
        mRefreshStateCallBack = recyclerViewRefreshStateCallBack;
    }

    public void setOnPullListener(OnPullListener onPullListener) {
        mOnPullListener = onPullListener;
    }


    public PullRefreshRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public PullRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        if (flag) {
            initView(context);
        }
    }

    public PullRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        if (flag) {
            initView(context);
        }
    }

    /**
     * 一个坑initAttrs方法里的typedArray去获取属性时，第一次获取的属性全是0，他会马上重走一次构造方法，再次获取一次，才能获得正确的值
     * 如果第一次获取的值为0，则不去initView
     * <p>
     * 由于用户可能没有设置refreshHeadViewId，也会获得为0，所以不能以refreshHeadViewId=0作为判断
     * 在这里以flag作为一个标识，第一遍走initAttrs时，将flag设为true，也就是第二次才走initView
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshRecyclerView);
        try {
            refreshHeaderViewId = typedArray.getResourceId(R.styleable.PullRefreshRecyclerView_refresh_header_view, 0);
            flag = true;
        } finally {
            typedArray.recycle();
        }

    }


    private void initView(Context context) {
        setOrientation(VERTICAL);
        mScroller = new Scroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        if (refreshHeaderViewId == 0) {
            mRefreshHeaderView = LayoutInflater.from(context).inflate(R.layout.recyclerview_headerview_moren, this, false);
            mRefreshStateCallBack = new ImplRecyclerViewRefreshStateCall(this);
        } else {
            mRefreshHeaderView = LayoutInflater.from(context).inflate(refreshHeaderViewId, this, false);
            if (mRefreshStateCallBack == null) {
                throw new RuntimeException("由于您使用了自定义的头布局，你要使用setRecyclerViewRefreshStateCall()方法，自定义一个该布局的动画效果,可参照ImplRecyclerViewRefreshStateCall");
            }
        }
        addView(mRefreshHeaderView);

        /*主要是为了设置头布局的marginTop值为-headerViewHeight
         *注意必须等到一小会才会得到正确的头布局宽高，滑动时差
         **/
        postDelayed(new Runnable() {
            @Override
            public void run() {

                refreshHeaderWidth = mRefreshHeaderView.getWidth();
                refreshHeaderHeight = mRefreshHeaderView.getHeight();

                MarginLayoutParams lp = new LayoutParams(refreshHeaderWidth, refreshHeaderHeight);
                lp.setMargins(0, -refreshHeaderHeight, 0, 0);
                mRefreshHeaderView.setLayoutParams(lp);

            }
        }, 10);

        mRecyclerView = new RecyclerView(context);
        addView(mRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLoadMore();
    }

    //当目前的可见条目是所有数据的最后一个时，开始加载新的数据
    private void setLoadMore() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastCompletelyVisibleItemPosition = -1;
                if (mLayoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager manager = (LinearLayoutManager) mLayoutManager;
                    lastCompletelyVisibleItemPosition = manager.findLastVisibleItemPosition();
                } else if (mLayoutManager instanceof GridLayoutManager) {
                    GridLayoutManager manager = (GridLayoutManager) mLayoutManager;
                    lastCompletelyVisibleItemPosition = manager.findLastVisibleItemPosition();
                } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) mLayoutManager;
                    lastCompletelyVisibleItemPosition = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()])[0];
                }
                if (lastCompletelyVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    if (mOnPullListener != null && state == DEFAULT) {
                        state = LOAD_MORE;
                        mOnPullListener.onLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();    //触摸点相对于view的位置
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (state != DEFAULT || state != REFRESHING) {  //当不是默认状态和刷新状态时，并且上一个动画没有停止，此时立即停止该动画
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;

                int firstCompletelyVisibleItemPosition = -1;
                if (mLayoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager manager = (LinearLayoutManager) mLayoutManager;
                    firstCompletelyVisibleItemPosition = manager.findFirstCompletelyVisibleItemPosition();
                }

                /**
                 * 视图内容相对于视图起始坐标的x/y轴方向的偏移量
                 *
                 * getScrollX:向右结果为负，向左结果为正
                 *
                 * getScrollY:向下结果为负，向上结果为正
                 */

                //拉到最顶部，继续往下拉(Y轴的偏移量大于X轴的偏移量)，将拉出头布局，需要父布局拦截
                if (firstCompletelyVisibleItemPosition == 0 && deltaY > 0 && Math.abs(deltaY) > Math.abs(deltaX)) {
                    intercepted = true;
                } else if (getScrollY() < 0 && Math.abs(deltaY) > Math.abs(deltaX)) {   //表示头布局已经向下拉出来，头布局已经显示，要父布局拦截
                    intercepted = true;
                } else if (deltaY < 0) { //向上滑动的时候
                    intercepted = false;
                } else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }

        mLastX = x;
        mLastY = y;
        mLastXIntercept = x;
        mLastYIntercept = y;

        Log.e(TAG, "onInterceptTouchEvent mLastX: " + mLastX);
        Log.e(TAG, "onInterceptTouchEvent mLastY: " + mLastY);
        Log.e(TAG, "onInterceptTouchEvent mLastXIntercept: " + mLastXIntercept);
        Log.e(TAG, "onInterceptTouchEvent mLastYIntercept: " + mLastYIntercept);


        return intercepted;
    }


    /**
     * 下面不同的布局，不同的滑动需求
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastY;

                /**
                 * 当scrollTo()/scrollBy()的传入参数为负的时候，view就向坐标轴正方向滚动；
                 * 当为正的时候，view就向坐标轴负方向滚动。
                 */

                if (getScrollY() > 0) { //防止正在刷新的状态下，上拉出现空白

                } else if (getScrollY() <= 0 && getScrollY() > -refreshHeaderHeight * 5) {  //最多下拉到头布局高度5倍的距离
                    scrollBy(0, -deltaY / 2); //向下滚动
                }

                //头布局显示不全时，为下拉刷新PULL_DOWN_REFRESH
                if (getScrollY() > -refreshHeaderHeight && state != REFRESHING) {
                    state = PULL_DOWN_REFRESH;
                    if (mRefreshStateCallBack != null) {
                        mRefreshStateCallBack.onPullDownRefreshState(getScrollY(), refreshHeaderHeight, deltaY);
                    }
                }

                //头布局显示完全时，为释放刷新RELEASE_REFRESH状态
                if (getScrollY() < -refreshHeaderHeight && state != REFRESHING) {
                    state = RELEASE_REFRESH;
                    if (mRefreshStateCallBack != null) {
                        mRefreshStateCallBack.onReleaseRefreshState(getScrollY(), deltaY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int scrollY = getScrollY();
                switch (state) {
                    case PULL_DOWN_REFRESH:
                        state = DEFAULT;
                        //头部没有完全显示，完全隐藏头部
                        smoothScrollBy(-scrollY);
                        break;
                    case RELEASE_REFRESH:
                        state = REFRESHING;
                        smoothScrollBy(-refreshHeaderHeight - scrollY);  //隐藏没有动画的部分

                        if (mRefreshStateCallBack != null) {//通知动画刷新
                            mRefreshStateCallBack.onRefreshingState();
                        }

                        if (mOnPullListener != null) {  //通知数据刷新
                            mOnPullListener.onRefresh();
                        }
                        break;
                    case REFRESHING:
                        if (scrollY < -refreshHeaderHeight) {
                            smoothScrollBy(-refreshHeaderHeight - scrollY);
                        } else {
                            smoothScrollBy(-scrollY);
                        }
                        break;
                }
                mVelocityTracker.clear();
                break;
        }

        mLastX = x;
        mLastY = y;

        return true;
    }

    /**
     * 在500ms内平滑的滚动多少像素点
     *
     * @param dy
     */
    private void smoothScrollBy(int dy) {
        mScroller.startScroll(0, getScrollY(), 0, dy, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    /**
     * 当用户下拉刷新完成回调时，调用此方法，将头部隐去，state状态恢复
     */
    public void refreshFinish() {
        smoothScrollBy(0 - getScrollY());
        getRecyclerView().getAdapter().notifyDataSetChanged();
        state = DEFAULT;
        if (mRefreshStateCallBack != null) {
            mRefreshStateCallBack.onDefaultState();
        }
        Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 当用户加载更多完成后回调，调用此方法，将state恢复
     */
    public void loadMoreFinish() {
        getRecyclerView().getAdapter().notifyDataSetChanged();
        state = DEFAULT;
        Toast.makeText(getContext(), "加载成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 回调接口
     * 加载不同的数据
     */
    public interface OnPullListener {

        /**
         * 当下拉刷新正在刷新时，这时候可以去请求数据，记得最后调用refreshFinish()复位
         */
        void onRefresh();

        /**
         * 当加载更多时
         */
        void onLoadMore();
    }

    /**
     * 回调接口，定义各种状态的显示效果
     * 可以根据下拉距离scrollY设计动画效果
     */
    public interface RecyclerViewRefreshStateCallBack {

        /**
         * 当处于下拉刷新时，头布局显示效果
         *
         * @param scrollY        下拉的距离
         * @param headViewHeight 头布局高度
         * @param deltaY         moveY-lastMoveY,正值为向下拉
         */
        void onPullDownRefreshState(int scrollY, int headViewHeight, int deltaY);


        /**
         * 当处于松手刷新时，头布局显示效果
         *
         * @param scrollY 下拉的距离
         * @param deltaY  moveY-lastMoveY,正值为向下拉
         */
        void onReleaseRefreshState(int scrollY, int deltaY);


        /**
         * 正在刷新时，页面的显示效果
         */
        void onRefreshingState();


        /**
         * 默认状态时，页面显示效果，主要是为了复位各种状态
         */
        void onDefaultState();
    }
}
