package com.blend.architecture.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.model.AsyncMessage;
import com.blend.architecture.eventbus.model.BackgroundMessage;
import com.blend.architecture.eventbus.model.MainMessage;
import com.blend.architecture.eventbus.model.PostingMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EventBusMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EventBusMainActivity";

    private Button btnMain, btnBackground, btnAsync, btnPosting, btn1;
    private TextView tv_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus_main);
        EventBus.getDefault().register(this);

        btnMain = findViewById(R.id.btnMain);
        btnBackground = findViewById(R.id.btnBackground);
        btnAsync = findViewById(R.id.btnAsync);
        btnPosting = findViewById(R.id.btnPosting);
        btn1 = findViewById(R.id.btn1);
        tv_desc = findViewById(R.id.tv_desc);

        btnMain.setOnClickListener(this);
        btnBackground.setOnClickListener(this);
        btnAsync.setOnClickListener(this);
        btnPosting.setOnClickListener(this);
        btn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnMain) {
            EventBus.getDefault().post(new MainMessage("MainMessage"));
        } else if (id == R.id.btnBackground) {
            EventBus.getDefault().post(new BackgroundMessage("BackgroundMessage"));
        } else if (id == R.id.btnAsync) {
            EventBus.getDefault().post(new AsyncMessage("AsyncMessage"));
        } else if (id == R.id.btnPosting) {
            EventBus.getDefault().post(new PostingMessage("PostingMessage"));
        } else if (id == R.id.btn1) {
            Intent intent = new Intent(EventBusMainActivity.this, EventBusSecondActivity.class);
            startActivity(intent);
        }
    }

    //主线程中执行
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MainMessage msg) {
        Log.e(TAG, msg.message);
        tv_desc.setText(msg.message);
    }

    //后台线程
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEventBus(BackgroundMessage msg) {
        Log.e(TAG, msg.message);
    }

    //异步线程
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAsyncEventBus(AsyncMessage msg) {
        Log.e(TAG, msg.message);
    }

    //默认情况，和发送事件在同一个线程
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPostEventBus(PostingMessage msg) {
        Log.e(TAG, msg.message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}