package com.blend.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blend.ui.MenuDrawerLayout.MenuDrawerLayoutActivity;
import com.blend.ui.animation.AnimationMainActivity;
import com.blend.ui.animation_framework.AnimatorFrameworkMainActivity;
import com.blend.ui.animation_view.ViewAnimationActivityActivity;
import com.blend.ui.animator.MyAnimatorActivity;
import com.blend.ui.custom_recycler.CustomRecyclerViewActivity;
import com.blend.ui.custom_view.CustomViewActivity;
import com.blend.ui.flowlayout.FlowLayoutActivity;
import com.blend.ui.item_touch_event.ItemTouchMainActivity;
import com.blend.ui.material_design.MaterialDesignMainActivity;
import com.blend.ui.nested_scrolling.NestedScrollingActivity;
import com.blend.ui.paint_gradient.PaintGradientActivity;
import com.blend.ui.qq_header_scrollerview.QQHeaderActivity;
import com.blend.ui.recyclerview.RecyclerViewActivity;
import com.blend.ui.recyclerview.RefreshRecyclerViewActivity;
import com.blend.ui.viewstub.ViewStubActivity;

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
    private Button animationBtn;
    private Button myAnimatorBtn;
    private Button animatorFrameworkBtn;
    private Button materialDesignBtn;
    private Button refreshRecyclerViewBtn;
    private Button nestedScrollingBtn;
    private Button menuDrawerLayoutBtn;
    private Button customViewBtn;
    private Button qqHeaderBtn;
    private Button customRecyclerViewBtn;
    private Button recyclerViewBtn;
    private Button viewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_main);
        flowLayoutBtn = findViewById(R.id.flowLayoutBtn);
        paintGradientBtn = findViewById(R.id.paintGradientBtn);
        itemTouchBtn = findViewById(R.id.itemTouchBtn);
        animatorBtn = findViewById(R.id.animatorBtn);
        animationBtn = findViewById(R.id.animationBtn);
        myAnimatorBtn = findViewById(R.id.myAnimatorBtn);
        animatorFrameworkBtn = findViewById(R.id.animatorFrameworkBtn);
        materialDesignBtn = findViewById(R.id.materialDesignBtn);
        refreshRecyclerViewBtn = findViewById(R.id.refreshRecyclerViewBtn);
        nestedScrollingBtn = findViewById(R.id.nestedScrollingBtn);
        menuDrawerLayoutBtn = findViewById(R.id.menuDrawerLayoutBtn);
        customViewBtn = findViewById(R.id.customViewBtn);
        qqHeaderBtn = findViewById(R.id.qqHeaderBtn);
        customRecyclerViewBtn = findViewById(R.id.customRecyclerViewBtn);
        recyclerViewBtn = findViewById(R.id.recyclerViewBtn);
        viewStub = findViewById(R.id.viewStub);

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

        refreshRecyclerViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, RefreshRecyclerViewActivity.class));
            }
        });

        nestedScrollingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, NestedScrollingActivity.class));
            }
        });

        menuDrawerLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, MenuDrawerLayoutActivity.class));
            }
        });

        customViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, CustomViewActivity.class));
            }
        });

        qqHeaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, QQHeaderActivity.class));
            }
        });

        customRecyclerViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, CustomRecyclerViewActivity.class));
            }
        });

        viewStub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, ViewStubActivity.class));
            }
        });

        animationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, ViewAnimationActivityActivity.class));
            }
        });

        myAnimatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, MyAnimatorActivity.class));
            }
        });

        recyclerViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UiMainActivity.this, RecyclerViewActivity.class));
            }
        });
    }

}
