package com.blend.architecture.plug_in.hook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;


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
        editor.putBoolean("login",false);   //设置保存的数据
        Toast.makeText(this, "退出登录成功",Toast.LENGTH_SHORT).show();
        editor.commit();    //提交数据保存
    }

}
