package com.blend.ui.paint_gradient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * canvas.save用于保存绘图坐标系。
 * canvas.translate()/canvas.rotate()/canvas.concat(Matrix)等操作的都是平移，旋转绘图坐标系。
 * canvas.restore();用于恢复绘图坐标系，也就是canvas的坐标系。
 * <p>
 * 自己总结的一点：
 * 要是使用4种着色器Shader，可以使用着色器的setLocalMatrix来改变着色器，从而改变平移，旋转等效果。
 * 要是没有使用着色器，就得结合canvas.save和canvas.restore()中的旋转/平移等操作绘图坐标系来实现相应的效果。
 */
public class PaintGradientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_paint_gradient);

        setContentView(new RadarGradientView(this));    //雷达
        // setContentView(new HandDraw(this));    //手写，使用双缓冲机制，先写到bitmap上，在画出来，一个绘制路线，一个绘制结果
        // setContentView(new TestCanvas(this));    //测试
        // setContentView(new ZoomImageView(this));              //放大镜
        // setContentView(new MyGradientView(this));    //ShapeDrawable
        // setContentView(new TelescopeView(this));    //望远镜
    }
}
