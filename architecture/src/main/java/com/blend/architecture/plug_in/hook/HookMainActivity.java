package com.blend.architecture.plug_in.hook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;

/**
 * Hook技术介绍：
 * 钩子函数实际上是一个处理消息的程序段，通过系统调用，把它挂入系统。在系统没有到调用该函数之前，钩子程序就先捕获该消息，这样钩子函数先得到控制权。
 * 这时钩子函数即可以加工处理（改变）该函数的执行行为,还可以强制结束消息的传递。
 * HOOK技术实现途径
 * 第一 :找到hook点
 * 第二 :将hook方法放到系统之外执行
 * <p>
 * 本例子是Hook AMS服务实现大型登陆架构，与AOP进行比较。
 */

public class HookMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_main);
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
