package com.blend.architecture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blend.architecture.aop.AopMainActivity;
import com.blend.architecture.change_skin.SkinMainActivity;
import com.blend.architecture.dagger2.zhuruyilai.ZhuruyilaiMainActivity;
import com.blend.architecture.database_design.DatabaseMainActivity;
import com.blend.architecture.eventbus.EventBusMainActivity;
import com.blend.architecture.eventbus.hermesevent.HermesEventBusMainActivity;
import com.blend.architecture.fragment.FragmentMainActivity;
import com.blend.architecture.glide.GlideMainActivity;
import com.blend.architecture.handle_message.ActivityThreadActivity;
import com.blend.architecture.okhttp.OkHttpMainActivity;
import com.blend.architecture.plug_in.hook.HookMainActivity;
import com.blend.architecture.plug_in.proxy.PlugInMainActivity;
import com.blend.architecture.retrofit.RetrofitMainActivity;
import com.blend.architecture.router.ARouterMainActivity;
import com.blend.architecture.rxjava.RxJavaMainActivity;

public class ArchitectureMainActivity extends AppCompatActivity {

    private Button handleMessage;
    private Button aop;
    private Button eventBus;
    private Button hermesEventBus;
    private Button changeSkin;
    private Button dataBaseDesign;
    private Button aRouter;
    private Button glide;
    private Button plugIn;
    private Button hook;
    private Button rxJava;
    private Button okHttp;
    private Button retrofit;
    private Button fragment;
    private Button dagger2;

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
        plugIn = findViewById(R.id.plugIn);
        hook = findViewById(R.id.hook);
        rxJava = findViewById(R.id.rxJava);
        okHttp = findViewById(R.id.okHttp);
        retrofit = findViewById(R.id.retrofit);
        fragment = findViewById(R.id.fragment);
        dagger2 = findViewById(R.id.dagger2);

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

        plugIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, PlugInMainActivity.class));
            }
        });

        hook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, HookMainActivity.class));
            }
        });

        rxJava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, RxJavaMainActivity.class));
            }
        });

        okHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, OkHttpMainActivity.class));
            }
        });

        retrofit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, RetrofitMainActivity.class));
            }
        });

        fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, FragmentMainActivity.class));
            }
        });

        dagger2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ArchitectureMainActivity.this, ZhuruyilaiMainActivity.class));
            }
        });
    }
}
