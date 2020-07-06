package com.blend.architecture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blend.architecture.aop.AopMainActivity;
import com.blend.architecture.eventbus.EventBusMainActivity;
import com.blend.architecture.eventbus.hermesevent.HermesEventBusMainActivity;
import com.blend.architecture.handle_message.ActivityThreadActivity;

public class ArchitectureMainActivity extends AppCompatActivity {

    private Button handleMessage;
    private Button aop;
    private Button eventBus;
    private Button hermesEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_architecture_main);
        handleMessage = findViewById(R.id.handleMessage);
        eventBus = findViewById(R.id.eventBus);
        aop = findViewById(R.id.aop);
        hermesEventBus = findViewById(R.id.hermesEventBus);
        handleMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, ActivityThreadActivity.class));
            }
        });

        aop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, AopMainActivity.class));
            }
        });
        eventBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, EventBusMainActivity.class));
            }
        });
        hermesEventBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, HermesEventBusMainActivity.class));
            }
        });
    }
}
