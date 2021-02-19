package com.blend.ui.paint_gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

class TestCanvas extends View {

    private TextPaint paint;

    public TestCanvas(Context context) {
        super(context);
    }

    public TestCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint = new TextPaint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        paint.setTextSize(24);

        //设置渐变器
        Shader shader = new LinearGradient(0, 0, 100, 0, new int[]{Color.RED, Color.GREEN}, null, Shader.TileMode.REPEAT);
        paint.setShader(shader);

        //设置阴影
        paint.setShadowLayer(25, 20, 20, Color.GRAY);

        canvas.save();

        //平移绘图坐标系，还有scale,rotate,skew，这四种操作的是绘图坐标系
        canvas.translate(0, 100);

        //绘制文本，文本是从文字的左下角为绘制圆点的
        canvas.drawText("绘制正常文本", 0, 0, paint);
        // canvas.restore();

        //绘制矩形
        canvas.drawRect(0, 0, 100, 100, paint);

        //绘制三角形，利用Path
        Path path = new Path();
        path.moveTo(50, 100);
        path.lineTo(100, 150);
        path.lineTo(0, 150);
        path.close();
        canvas.drawPath(path, paint);

        //设置放射性渐变器
        paint.setShader(new RadialGradient(150, 150, 100,
                new int[]{Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN}, new float[]{0.1f, 0.3f, 0.6f, 0.9f},
                Shader.TileMode.MIRROR));

        //将这个渐变器应用到这个图形上
        canvas.drawRect(100, 100, 200, 200, paint);
    }
}
