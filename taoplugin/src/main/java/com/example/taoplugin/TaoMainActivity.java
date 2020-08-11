package com.example.taoplugin;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class TaoMainActivity extends BaseActivity {

    private static final String REGISTER = "com.blend.tao.dynamic.broadcast.TaoMainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_main);

        findViewById(R.id.registerBroad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动态注册广播
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(REGISTER);
                registerReceiver(new MyReceiver(), intentFilter);
            }
        });

        findViewById(R.id.sendBroad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(REGISTER);
                sendBroadcast(intent);
            }
        });

        findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(that, "插件", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(that, SecondActivity.class));
                startService(new Intent(that, OneService.class));
            }
        });
    }
}
