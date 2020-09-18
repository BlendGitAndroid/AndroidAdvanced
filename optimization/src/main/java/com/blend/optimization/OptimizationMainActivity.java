package com.blend.optimization;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blend.optimization.startup.AppStartupMainActivity;

public class OptimizationMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimization_main);

        findViewById(R.id.appStartup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptimizationMainActivity.this, AppStartupMainActivity.class));
            }
        });
    }
}