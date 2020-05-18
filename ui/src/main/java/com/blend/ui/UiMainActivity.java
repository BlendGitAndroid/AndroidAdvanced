package com.blend.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blend.ui.animation.AnimationMainActivity;
import com.blend.ui.animation_framework.AnimatorFrameworkMainActivity;
import com.blend.ui.flowlayout.FlowLayoutActivity;
import com.blend.ui.item_touch_event.ItemTouchMainActivity;
import com.blend.ui.material_design.MaterialDesignMainActivity;
import com.blend.ui.paint_gradient.PaintGradientActivity;

/**
 * 1.属性动画的问题？
 * <p>
 * 2.ItemTouchHelp中的bug？
 * <p>
 * 3.屏幕适配？
 */

public class UiMainActivity extends AppCompatActivity {

    private Button flowLayoutBtn;
    private Button paintGradientBtn;
    private Button itemTouchBtn;
    private Button animatorBtn;
    private Button animatorFrameworkBtn;
    private Button materialDesignBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_main);
        flowLayoutBtn = findViewById(R.id.flowLayoutBtn);
        paintGradientBtn = findViewById(R.id.paintGradientBtn);
        itemTouchBtn = findViewById(R.id.itemTouchBtn);
        animatorBtn = findViewById(R.id.animatorBtn);
        animatorFrameworkBtn = findViewById(R.id.animatorFrameworkBtn);
        materialDesignBtn = findViewById(R.id.materialDesignBtn);

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
        animatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, AnimationMainActivity.class));
            }
        });

        animatorFrameworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, AnimatorFrameworkMainActivity.class));
            }
        });

        materialDesignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, MaterialDesignMainActivity.class));
            }
        });
    }
}
