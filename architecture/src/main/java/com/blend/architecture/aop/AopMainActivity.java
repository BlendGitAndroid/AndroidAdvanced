package com.blend.architecture.aop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blend.architecture.R;
import com.blend.architecture.aop.animal.Animal;
import com.blend.architecture.aop.annotation.BehaviorTrace;
import com.blend.architecture.aop.annotation.UserInfoBehaviorTrace;

/**
 * AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护
 * 的一种技术。AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。
 * 利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。
 * AOP是一种面向切面编程的技术的统称，AOP框架最终都会围绕class字节码的操作展开，无论是对字节码的操作增删改，为方便描述，我们统称为
 * 代码的织入 。
 * <p>
 * 切面：对一类行为的抽象，是切点的集合，比如在用户访问所有模块前做的权限认证。
 * 切点：描述切面的具体的一个业务场景。
 * 通知(Advice)类型：通常分为切点前、切点后和切点内，比如在方法前织入代码是指切点前。
 * <p>
 * 本文所用的AspectJ，可以实现一些简单的AOP需求，它的工作原理是：通过Gradle Transform，在class文件生成后至dex文件生
 * 成前，遍历并匹配所有符合AspectJ文件中声明的切点，然后将事先声明好的代码在切点前后织入，是一种静态织入方式。
 *
 * <p>
 * 作用：统计埋点，日志打印/打点，数据校验，行为拦截，性能监控，动态权限控制。
 */
public class AopMainActivity extends AppCompatActivity {

    private static final String TAG = "AopMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aop_main);
    }

    //摇一摇
    @UserInfoBehaviorTrace("摇一摇")
    @BehaviorTrace("摇一摇")
    public void mShake(View view) {
        Log.e(TAG, "mAudio: 我在发摇一摇");
    }

    //语音消息
    @BehaviorTrace("语音消息")
    public void mAudio(View view) {
        Log.e(TAG, "mAudio: 我在发语音消息");
    }

    //视频通话
    @BehaviorTrace("视频通话")
    public void mVideo(View view) {
        Log.e(TAG, "mAudio: 我在视频通话");
    }

    //发表说说
    @BehaviorTrace("发表说说")
    public void saySomething(View view) {
        Log.e(TAG, "mAudio: 我在发表说说");
    }

    //方法织入
    public void animalFly(View view) {
        new Animal().fly();
    }
}