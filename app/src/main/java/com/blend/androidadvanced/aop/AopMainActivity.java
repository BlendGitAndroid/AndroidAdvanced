package com.blend.androidadvanced.aop;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blend.androidadvanced.R;
import com.blend.androidadvanced.aop.annotation.BehaviorTrace;
import com.blend.androidadvanced.aop.annotation.UserInfoBehaviorTrace;

import java.util.Random;

/**
 * AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护
 * 的一种技术。AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。
 * 利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。
 * <p>
 * 作用：App中很多跳转的地方都需要登入校验，网络判断，权限管理，Log日志的统一管理。
 */
public class AopMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aop_main);
    }

    //摇一摇
    @UserInfoBehaviorTrace("摇一摇")
    @BehaviorTrace("摇一摇")
    public void mShake(View view) {
        long begin = System.currentTimeMillis();
        SystemClock.sleep(new Random().nextInt(2000));
        long duration = System.currentTimeMillis() - begin;
    }

    //语音消息
    @BehaviorTrace("语音消息")
    public void mAudio(View view) {

    }

    //视频通话
    @BehaviorTrace("视频通话")
    public void mVideo(View view) {
    }

    //发表说说
    @BehaviorTrace("发表说说")
    public void saySomething(View view) {
    }
}