package com.blend.androidadvanced;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blend.androidadvanced.aop.AopMainActivity;
import com.blend.architecture.ArchitectureMainActivity;

public class MainActivity extends Activity {

    private Button aop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aop = findViewById(R.id.aop);

        aop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AopMainActivity.class));
            }
        });

        // //高级UI
        // // startActivity(new Intent(this, UiMainActivity.class));
        //
        // //移动架构
        // startActivity(new Intent(this, ArchitectureMainActivity.class));
        // finish();
    }
}
