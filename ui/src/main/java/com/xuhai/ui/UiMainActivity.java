package com.xuhai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xuhai.ui.flowlayout.FlowLayoutActivity;

public class UiMainActivity extends AppCompatActivity {

    private Button flowLayoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_main);
        flowLayoutBtn = findViewById(R.id.flowLayoutBtn);
        flowLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, FlowLayoutActivity.class));
            }
        });
    }
}
