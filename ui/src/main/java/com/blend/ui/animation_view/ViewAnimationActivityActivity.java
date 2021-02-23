package com.blend.ui.animation_view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.blend.ui.R;

/**
 * View动画，也叫作视图动画，也叫作补间动画。
 * AnimationSet是一个特殊的Animation，对它设置的插值器，会应用到它所有的子Animation上。
 */
public class ViewAnimationActivityActivity extends AppCompatActivity {

    private ImageView ivShumiao;
    private ImageView ivShuihu;
    private ImageView ivWater;

    int shuiHuX = 0;
    int shuiHuY = 0;

    int shuMiaoX = 0;
    int shuMiaoY = 0;

    int shuMiaoWidth = 0;
    int shuMiaoHeight = 0;

    int shuiHuWidth = 0;
    int shuiHuHeight = 0;

    int waterHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_animation_activity);
        ivShumiao = findViewById(R.id.iv_shumiao);
        ivShuihu = findViewById(R.id.iv_shuihu);
        ivWater = findViewById(R.id.iv_water);

        ivShuihu.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                ivShuihu.getLocationOnScreen(location);
                shuiHuX = location[0]; // view距离 屏幕左边的距离（即x轴方向）
                shuiHuY = location[1]; // view距离 屏幕顶边的距离（即y轴方向）
                shuiHuWidth = ivShuihu.getWidth();
                shuiHuHeight = ivShuihu.getHeight();
            }
        });

        ivShumiao.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                ivShumiao.getLocationOnScreen(location);
                shuMiaoX = location[0]; // view距离 屏幕左边的距离（即x轴方向）
                shuMiaoY = location[1]; // view距离 屏幕顶边的距离（即y轴方向）

                shuMiaoWidth = ivShumiao.getWidth(); // 获取宽度
                shuMiaoHeight = ivShumiao.getHeight(); // 获取高度
            }
        });

        ivWater.post(new Runnable() {
            @Override
            public void run() {
                waterHeight = ivWater.getHeight();
            }
        });


        ivShuihu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();

            }
        });
    }

    private void start() {

        AnimationSet setAnimation = new AnimationSet(true);

        // 子动画1:旋转动画
        Animation rotate = new RotateAnimation(0, 10, 0.5f, 0.5f);
        rotate.setDuration(1000);
        //调用后等待3秒开始执行
        rotate.setStartOffset(3000);

        // 子动画2:平移动画
        Animation translate = new TranslateAnimation(
                0,
                shuMiaoX - shuiHuWidth,
                0
                , -(shuiHuHeight + shuMiaoHeight + waterHeight + 100));
        translate.setDuration(3000);

        //等待水滴落下动画，这一步不能省略，要是省略了，当水壶动画完成后，就会回到原点
        //因为不能设置先后顺序，先后顺序的设置只能通过setStartOffset
        Animation wait = new TranslateAnimation(
                0,
                100,
                0
                , 100);
        wait.setStartOffset(4000);
        wait.setDuration(2000);


        // 步骤4:将创建的子动画添加到组合动画里
        setAnimation.addAnimation(translate);
        setAnimation.addAnimation(rotate);
        setAnimation.addAnimation(wait);
        setAnimation.setInterpolator(new BounceInterpolator());

        ivShuihu.startAnimation(setAnimation);


        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivWater.setVisibility(View.VISIBLE);
                startWaterAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void startWaterAnimation() {

        AnimationSet animationSet = new AnimationSet(true);

        //平移动画
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 50);
        translateAnimation.setDuration(2000);

        //透明度动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(2000);


        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setRepeatMode(Animation.RESTART);
        ivWater.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivWater.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


}