package com.blend.ui.animtation;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.blend.ui.R;

public class SplashActivity extends AppCompatActivity {

    private FrameLayout mFrameLayout;
    private SplashAnimationView splashAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFrameLayout = new FrameLayout(this);

        //将动画层盖在实际的操作图上
        SplashImageVIew imageVIew = new SplashImageVIew(this);
        mFrameLayout.addView(imageVIew);

        splashAnimationView = new SplashAnimationView(this);
        mFrameLayout.addView(splashAnimationView);

        setContentView(mFrameLayout);

        startLoadData();
    }


    private void startLoadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashAnimationView.splashDisappear();
            }
        }, 5000);
    }
}
