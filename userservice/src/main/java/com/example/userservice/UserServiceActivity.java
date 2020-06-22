package com.example.userservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class UserServiceActivity extends Activity {

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_service);
        //策略模式
        mButton = findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void test(){

        //单例模式
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        //Builder模式
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("test").setMessage("message").show();

        //原型模式
        Intent intent = new Intent();
        intent.clone();

        //工厂方法模式
        setContentView(new TextView(this));


        //迭代器模式
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator iterator = arrayList.iterator();

        HashMap<String,Integer> hashMap = new HashMap<>();
        hashMap.entrySet().iterator();
    }

    //备忘录模式
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
