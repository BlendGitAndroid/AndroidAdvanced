package com.blend.architecture.eventbus.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.model.MainMessage;

import org.greenrobot.eventbus.EventBus;

public class CoreEventBusSecondActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnCoreSecondMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_event_bus_second);
        btnCoreSecondMain = findViewById(R.id.btnCoreSecondMain);
        btnCoreSecondMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCoreSecondMain) {
            CoreEventBus.getDefault().post(new MainMessage("传递信息：core event bus study!"));
            finish();
        }
    }
}