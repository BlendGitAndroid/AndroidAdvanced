package com.blend.androidadvanced;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blend.androidadvanced.ioc.IocMainActivity;
import com.blend.architecture.ArchitectureMainActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //高级UI
        // startActivity(new Intent(this, UiMainActivity.class));

        //移动架构
        // startActivity(new Intent(this, ArchitectureMainActivity.class));

        //IOC
        startActivity(new Intent(this, IocMainActivity.class));
        finish();
    }
}
