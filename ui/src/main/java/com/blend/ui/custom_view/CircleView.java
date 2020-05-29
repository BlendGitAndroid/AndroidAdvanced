package com.blend.ui.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.blend.ui.R;


public class CircleView extends View {

    private int mColor = Color.RED;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = a.getColor(R.styleable.CircleView_circleColorView, Color.RED);
        a.recycle();
        mPaint.setColor(mColor);
    }

    /**
     * 继承View和ViewGroup,给wrap_content设置默认宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpcMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpcSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpcMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpcSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpcMode == MeasureSpec.AT_MOST && heightSpcMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, 200);
        } else if (widthSpcMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, heightSpcSize);
        } else if (heightSpcMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpcSize, 200);
        }
    }

    /**
     * 继承View和ViewGroup,padding默认是不生效的，绘制的时候需要考虑一下
     * <p>
     * 这里要注意一下，padding的设定值和onMeasure里面默认宽高的设定值，不能大于默认宽高
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        int radius = Math.min(width, height) / 2;
        canvas.drawCircle(paddingLeft + width / 2, paddingTop + height / 2, radius, mPaint);
    }
}
