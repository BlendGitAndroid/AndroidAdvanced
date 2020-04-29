package com.blend.ui.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class FlowLayout2 extends ViewGroup {

    public FlowLayout2(Context context) {
        super(context);
    }

    public FlowLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取到自己的测量模式和最大允许的大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        //测量自己最大的宽高
        int measureWidth = 0;
        int measureHeight = 0;

        //当前行宽，行高，因为存在多行，下一行数据要放到下方，行高需要保存
        int iCurLineW = 0;
        int iCurLineH = 0;


        //1.确认自己当前空间的宽高，这里因为会有两次OnMeasure,进行二级测量优化，所以采用IF_ELSE结构
        //二级优化原理在源码具体Draw时，第一次不会直接进行performDraw的调用反而是在下面重新进行了一次scheduleTraversals
        //在ViewRootImpl源码2349-2372之中我门会看到  scheduleTraversals在我们的2363
        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        }

        //当前子VIEW宽高
        int iChildWidth = 0;
        int iChildHeight = 0;
        //获取子VIEW数量用于迭代
        int childCount = getChildCount();
        //单行信息容器
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            //1.测量自己
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            //2.获取XML资源
            MarginLayoutParams layoutParams = (MarginLayoutParams) childAt.getLayoutParams();


            //3.获得实际宽度和高度(MARGIN+WIDTH)
            iChildWidth = childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            iChildHeight = childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;


            //4.是否需要换行
            if (iCurLineW + iChildWidth > widthSize) {
                //4.1.纪录当前行信息
                //4.1.1.纪录当前行最大宽度，高度累加
                if (widthMode != MeasureSpec.EXACTLY) {
                    measureWidth = Math.max(measureWidth, iCurLineW);
                }

                if (heightMode != MeasureSpec.EXACTLY) {
                    measureHeight += iCurLineH;
                }
                //4.1.2.保存这一行数据，及行高

                //4.2.纪录新的行信息
                //4.2.1.赋予新行新的宽高
                iCurLineW = iChildWidth;
                iCurLineH = iChildHeight;


            } else {
                //5.1.不换行情况
                //5.1.1.记录某行内的消息行内宽度的叠加、高度比较
                iCurLineW += iChildWidth;
                iCurLineH = Math.max(iCurLineH, iChildHeight);

            }

            //6.如果正好是最后一行需要换行
            if (i == childCount - 1) {
                //6.1.记录当前行的最大宽度，高度累加
                if (widthMode != MeasureSpec.EXACTLY) {
                    measureWidth = Math.max(measureWidth, iCurLineW);
                }
                if (heightMode != MeasureSpec.EXACTLY) {
                    measureHeight += iCurLineH;
                }
            }
        }


        //确认保存自己的宽高
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //自定义子View的左和上的起始点
        int startX = getPaddingLeft();
        int startY = getPaddingTop();

        //自定义控件的测量宽高
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        //每个子控件占据的宽高
        int childViewUseWidth = 0;
        int childViewUseLineHeight = 0;

        //子控件的数量
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);

            if (childView.getVisibility() == GONE) {
                continue;
            }
            //获取每个子控件的layoutParams
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();

            int childViewMeasuredWidth = childView.getMeasuredWidth();
            int childViewMeasuredHeight = childView.getMeasuredHeight();

            //startX 变化为0 就换行， 每个子控件在摆放之前，判断剩余控件是否足够，用startX + childViewMeasuredWidth是否大于整个控件的剩余宽度
            //判断的时候考虑PaddingRight
            //考虑了子控件自己的margin值，每个子控件占据的宽度：childViewMeasuredWidth + leftMargin + rightMargin
            childViewUseWidth = childViewMeasuredWidth + layoutParams.leftMargin + layoutParams.rightMargin;
            if (startX + childViewUseWidth > measuredWidth - getPaddingRight()) {

                startX = getPaddingLeft();

                //换行的时候，上一行使用的高度以一行的最高的为准
                startY += childViewUseLineHeight; //y左边累加，因为现在所有的子控件高度都一样

                childViewUseLineHeight = 0;  //换行后，每一行的最大高度置为0

            }


            //摆放子控件
            int leftChildView = startX + layoutParams.leftMargin;//考虑自己的margin
            int topChildView = startY + layoutParams.topMargin;
            int rightChildView = leftChildView + childViewMeasuredWidth;
            int bottomChildView = topChildView + childViewMeasuredHeight;
            //子控件布局
            childView.layout(leftChildView, topChildView, rightChildView, bottomChildView);

            //子控件摆放之后累加startX的值, 考虑每个孩子占据的宽度要加上marginLeft , marginRingt
            startX += childViewUseWidth;

            //计算每一行使用的高度
            childViewUseLineHeight = Math.max(childViewUseLineHeight, childViewMeasuredHeight + layoutParams.topMargin + layoutParams.bottomMargin);
        }
    }
}
