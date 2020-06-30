package com.blend.architecture.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.model.MainMessage;

import org.greenrobot.eventbus.EventBus;

public class EventBusSecondActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSecondMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus_second);

        btnSecondMain = findViewById(R.id.btnSecondMain);
        btnSecondMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSecondMain) {
            EventBus.getDefault().post(new MainMessage("传递信息：event bus study!"));
            finish();
        }
    }
}