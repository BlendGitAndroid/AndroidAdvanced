package com.blend.architecture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blend.architecture.glide.GlideMainActivity;
import com.blend.architecture.router.ARouterMainActivity;
import com.blend.architecture.aop.AopMainActivity;
import com.blend.architecture.change_skin.SkinMainActivity;
import com.blend.architecture.database_design.DatabaseMainActivity;
import com.blend.architecture.eventbus.EventBusMainActivity;
import com.blend.architecture.eventbus.hermesevent.HermesEventBusMainActivity;
import com.blend.architecture.handle_message.ActivityThreadActivity;

public class ArchitectureMainActivity extends AppCompatActivity {

    private Button handleMessage;
    private Button aop;
    private Button eventBus;
    private Button hermesEventBus;
    private Button changeSkin;
    private Button dataBaseDesign;
    private Button aRouter;
    private Button glide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_architecture_main);
        handleMessage = findViewById(R.id.handleMessage);
        eventBus = findViewById(R.id.eventBus);
        aop = findViewById(R.id.aop);
        hermesEventBus = findViewById(R.id.hermesEventBus);
        changeSkin = findViewById(R.id.changeSkin);
        dataBaseDesign = findViewById(R.id.dataBaseDesign);
        aRouter = findViewById(R.id.aRouter);
        glide = findViewById(R.id.glide);

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
        changeSkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, SkinMainActivity.class));
            }
        });
        dataBaseDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, DatabaseMainActivity.class));
            }
        });
        aRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, ARouterMainActivity.class));
            }
        });

        glide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, GlideMainActivity.class));
            }
        });
    }
}
