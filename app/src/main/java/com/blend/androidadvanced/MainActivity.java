package com.blend.androidadvanced;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blend.algorithm.stack.MyStack;
import com.blend.androidadvanced.heuristic.HeuristicMainActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //高级UI
        // startActivity(new Intent(this, UiMainActivity.class));

        //移动架构
        // startActivity(new Intent(this, ArchitectureMainActivity.class));

        //IOC
        // startActivity(new Intent(this, IocMainActivity.class));

        // 性能优化
        // startActivity(new Intent(this, OptimizationMainActivity.class));
        // finish();

        //数据结构与算法
        MyStack.hanoi(3, "A", "B", "C");

        MyStack.monkeyStealPeach();

        //启发式寻路
        startActivity(new Intent(this, HeuristicMainActivity.class));

        //master分支做了一些更改
    }
}
