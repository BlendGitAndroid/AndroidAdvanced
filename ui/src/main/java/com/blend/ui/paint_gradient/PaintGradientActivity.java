package com.blend.ui.paint_gradient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PaintGradientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_paint_gradient);

        // setContentView(new RadarGradientView(this));    //雷达
        // setContentView(new HandDraw(this));    //手写，使用双缓冲机制，先写到bitmap上，在画出来，一个绘制路线，一个绘制结果
        // setContentView(new TestCanvas(this));    //测试
        // setContentView(new ZoomImageView(this));              //放大镜
        // setContentView(new MyGradientView(this));    //ShapeDrawable
        setContentView(new TelescopeView(this));    //望远镜
    }
}
