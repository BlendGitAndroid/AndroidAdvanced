package com.blend.ui.paint_gradient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.ui.R;

public class PaintGradientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_gradient);

        //setContentView(new RadarGradientView(this));    //雷达
        //setContentView(new ZoomImageView(this));              //放大镜
        //setContentView(new MyGradientView(this));
    }
}
