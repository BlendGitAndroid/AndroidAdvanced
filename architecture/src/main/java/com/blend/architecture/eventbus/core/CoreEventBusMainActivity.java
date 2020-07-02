package com.blend.architecture.eventbus.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.model.AsyncMessage;
import com.blend.architecture.eventbus.model.MainMessage;


public class CoreEventBusMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CoreEventBusMainActivit";

    private Button btnMain, btnAsync, btn1;
    private TextView tv_core_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_event_bus_main);

        CoreEventBus.getDefault().register(this);

        btnMain = findViewById(R.id.btnCoreMain);
        btnAsync = findViewById(R.id.btnCoreAsync);
        btn1 = findViewById(R.id.btnCore1);
        tv_core_desc = findViewById(R.id.tv_core_desc);

        btnMain.setOnClickListener(this);
        btnAsync.setOnClickListener(this);
        btn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnCoreMain) {
            CoreEventBus.getDefault().post(new MainMessage("CoreMainMessage"));
        } else if (id == R.id.btnCoreAsync) {
            CoreEventBus.getDefault().post(new AsyncMessage("CoreAsyncMessage"));
        } else if (id == R.id.btnCore1) {
            Intent intent = new Intent(CoreEventBusMainActivity.this, CoreEventBusSecondActivity.class);
            startActivity(intent);
        }
    }

    //主线程中执行
    @CoreSubscribe(threadMode = CoreThreadMode.MAIN)
    public void onMainEventBus(MainMessage msg) {
        Log.e(TAG, msg.message);
        tv_core_desc.setText(msg.message);
    }


    //异步线程
    @CoreSubscribe(threadMode = CoreThreadMode.ASYNC)
    public void onAsyncEventBus(AsyncMessage msg) {
        Log.e(TAG, msg.message);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreEventBus.getDefault().unregister(this);
    }
}