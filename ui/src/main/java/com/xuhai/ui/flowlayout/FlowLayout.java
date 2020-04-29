package com.xuhai.ui.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * bug:在7.0之后的手机上，显示的自定义控件的高度是本来高度的两倍
 * <p>
 * 原因：
 * <p>
 * 这个FlowLayout,首先看代码，采取的方式，是将测量后的代码用集合保存起来再给layout进行布局
 * <p>
 * 在7.0之前的版本，绘制过程：onMeasure -> onLayout -> onMeasure -> onLayout，在每一次onLayout之后都会讲集合清空
 * <p>
 * 但是，在7.0之后的手机上，绘制过程：onMeasure -> onMeasure -> onLayout，减少了一次onLayout,没有集合清空，两次onMeasure
 * <p>
 * 会使控件高度是原来的二倍
 * <p>
 * 解决办法：
 * 1.在onMeasure时第一步就进行集合清空操作。
 * 2.修改布局代码，见FlowLayout2。
 */
public class FlowLayout extends ViewGroup {

    private static final String TAG = "FlowLayout";

    //行高纪录
    List<Integer> lstHeights = new ArrayList<>();
    //每一行的视图
    List<List<View>> lstLineView = new ArrayList<>();  //既然是一一对应的关系，可以用Map

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
    根据父布局的MeasureSpec和自己的LayoutParam,确定自定义控件的MeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.e(TAG, "onMeasure");

        //清空集合，防止在7.0之后的版本高度变为原来的二倍
        lstHeights.clear();
        lstLineView.clear();

        //获取到自己的测量模式和最大允许的大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);


        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //测量自己最大的宽高
        int measureWidth = 0;
        int measureHeight = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        }

        //每一行的宽高
        int iCurLineW = 0;
        int iCurLineH = 0;

        //每一行的布局容器
        List<View> viewList = new ArrayList<>();


        //当前子View控件宽高
        int iChildWidth = 0;
        int iChildHeight = 0;

        //获取子View数量用于迭代
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            //1.测量每一个子View
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //2.获取每一个子View的getLayoutParams，即XML资源
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            //3.获取每一个子View控件的实际宽高
            iChildWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            iChildHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            //4.是否需要换行
            if (iCurLineW + iChildWidth > widthSize) {
                //记录总的布局最大宽度，高度累加
                if (widthMode != MeasureSpec.EXACTLY) {
                    measureWidth = Math.max(measureWidth, iCurLineW);
                }
                if (heightMode != MeasureSpec.EXACTLY) {
                    measureHeight += iCurLineH;
                }

                //保存每一行的数据（高度和View）
                lstHeights.add(iCurLineH);
                lstLineView.add(viewList);

                //赋予新的行宽高
                iCurLineW = iChildWidth;
                iCurLineH = iChildHeight;

                //添加新的行
                viewList = new ArrayList<>();
                viewList.add(child);

            } else {
                iCurLineW += iChildWidth;
                iCurLineH = Math.max(iCurLineH, iChildHeight);
                viewList.add(child);
            }

            //5如果是最后一行
            if (i == childCount - 1) {
                if (widthMode != MeasureSpec.EXACTLY) {
                    measureWidth = Math.max(measureWidth, iCurLineW);
                }
                if (heightMode != MeasureSpec.EXACTLY) {
                    measureHeight += iCurLineH;
                }

                lstHeights.add(iCurLineH);
                lstLineView.add(viewList);
            }
        }

        Log.e(TAG, "setMeasuredDimension: " + measureWidth + "--" + measureHeight);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.e(TAG, "onLayout");

        //1.每一个子控件的位置
        int left, top, right, bottom;

        //2.当前行的高度和宽度
        int curTop = 0;
        int curLeft = 0;

        for (int i = 0; i < lstLineView.size(); i++) {
            List<View> viewList = lstLineView.get(i);
            for (int j = 0; j < viewList.size(); j++) {
                View child = viewList.get(j);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                left = curLeft + layoutParams.leftMargin;
                top = curTop + layoutParams.topMargin;
                right = left + child.getMeasuredWidth();
                bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);

                curLeft += child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            }
            curTop += lstHeights.get(i);
            curLeft = 0;
        }
        lstLineView.clear();
        lstHeights.clear();

    }
}
