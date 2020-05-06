package com.blend.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blend.ui.flowlayout.FlowLayoutActivity;
import com.blend.ui.item_touch_event.ItemTouchMainActivity;
import com.blend.ui.paint_gradient.PaintGradientActivity;

public class UiMainActivity extends AppCompatActivity {

    private Button flowLayoutBtn;
    private Button paintGradientBtn;
    private Button itemTouchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_main);
        flowLayoutBtn = findViewById(R.id.flowLayoutBtn);
        paintGradientBtn = findViewById(R.id.paintGradientBtn);
        itemTouchBtn = findViewById(R.id.itemTouchBtn);

        flowLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, FlowLayoutActivity.class));
            }
        });
        paintGradientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, PaintGradientActivity.class));
            }
        });
        itemTouchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, ItemTouchMainActivity.class));
            }
        });
    }
}
