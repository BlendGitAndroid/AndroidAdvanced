package com.blend.architecture.plug_in.hook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Hook技术介绍：
 * 钩子函数实际上是一个处理消息的程序段，通过系统调用，把它挂入系统。在系统没有到调用该函数之前，钩子程序就先捕获该消息，这样钩子函数先得到控制权。
 * 这时钩子函数即可以加工处理（改变）该函数的执行行为,还可以强制结束消息的传递。
 * Hook 的这个本领，使它能够将自身的代码「融入」被勾住（Hook）的程序的进程中，成为目标进程的一个部分。API Hook 技术是一种用于改变 API 执行结
 * 果的技术，能够将系统的 API 函数执行重定向。在 Android 系统中使用了沙箱机制，普通用户程序的进程空间都是独立的，程序的运行互不干扰。这就使我们
 * 希望通过一个程序改变其他程序的某些行为的想法不能直接实现，但是 Hook 的出现给我们开拓了解决此类问题的道路。当然，根据 Hook 对象与 Hook 后处理
 * 的事件方式不同，Hook 还分为不同的种类，比如消息 Hook、API Hook 等。
 * <p>
 * HOOK技术实现途径：
 * 第一 :找到hook点，hook点一般选取静态变量和单例来实现，因为他们一般不容易改变，一般是一个对象。
 * 第二 :将hook方法放到系统之外执行。
 * <p>
 * Java中的HOOK主要通过反射和代理来实现，用于在SDK开发环境中修改Java代码。
 * <p>
 * 本例子是Hook：
 * 1.API Hook，Android的各种监听。
 * 2.AMS服务实现大型登陆架构，与AOP进行比较。
 * 网址：https://www.jianshu.com/p/4f6d20076922
 * <p>
 * 被劫持的对象叫做HOOK点，使用代理对象来代替HOOK点，实现自己想做的事情。
 */

public class HookMainActivity extends AppCompatActivity {

    private static final String TAG = "HookMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_main);
        hookTest();
        hookOnClickListener(findViewById(R.id.hookTest));
    }

    private void hookTest() {
        findViewById(R.id.hookTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "点击事件");
            }
        });
    }


    /**
     * 这个代码要放在设置Click事件之后，这个代码就是使用反射和代理实现的
     * setOnClickListener，设置的其实在View的内部类ListenerInfo中的mOnClickListener字段，Hook的时候
     * 先使用反射拿到该字段，然后使用代码创建一个View.OnClickListener类，使用代理模式，在onClick的调用前后
     * 加上自己需要的逻辑。最后将mOnClickListener设置成自己的Click类，那么Click回调就会回调自己的类。
     * 偷梁换柱。
     * 其实这也是AOP的一种思想。
     * <p>
     * Hook的本质就是用自己的代码替换掉原来的代码，用自己生成的对象替换掉原来的对象，这样就可以在原来的代码前后加上自己的逻辑。
     */
    private void hookOnClickListener(View view) {
        try {
            // 得到 View 的 ListenerInfo 对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);

            // 得到 原始的 OnClickListener 对象
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);

            // 用自定义的 OnClickListener 替换原始的 OnClickListener
            View.OnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickListener);
            mOnClickListener.set(listenerInfo, hookedOnClickListener);  //在这里替换赋值
        } catch (Exception e) {
            Log.e(TAG, "hookOnClickListener: ");
        }
    }

    class HookedOnClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(HookMainActivity.this, "hook click", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onClick: Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.e(TAG, "After click, do what you want to to.");
        }
    }

    public void jump2(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
//        系统里面做了手脚   --》newIntent   msg--->obj-->intent
        startActivity(intent);
    }

    public void jump3(View view) {
        Intent intent = new Intent(this, ThreeActivity.class);
        startActivity(intent);
    }

    public void jump4(View view) {
        Intent intent = new Intent(this, FourActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        SharedPreferences share = this.getSharedPreferences("blend", MODE_PRIVATE);//实例化
        SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
        editor.putBoolean("login", false);   //设置保存的数据
        Toast.makeText(this, "退出登录成功", Toast.LENGTH_SHORT).show();
        editor.commit();    //提交数据保存
    }

}
