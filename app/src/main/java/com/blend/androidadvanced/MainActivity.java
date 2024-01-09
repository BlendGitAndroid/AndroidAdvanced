package com.blend.androidadvanced;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.blend.architecture.ArchitectureMainActivity;
import com.blend.ui.UiMainActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        //高级UI
        // startActivity(new Intent(this, UiMainActivity.class));

        //移动架构
        startActivity(new Intent(this, ArchitectureMainActivity.class));

        //IOC
        // startActivity(new Intent(this, IocMainActivity.class));
        // Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("blend://android:78"));
        // intent.addCategory("android.blend");
        // intent.addCategory("android.intent.category.DEFAULT");
        // startActivity(intent);

        // 性能优化
        // startActivity(new Intent(this, OptimizationMainActivity.class));
        // finish();

        //数据结构与算法
        // AlgorithmClass.algorithm();

        verifyStoragePermissions();
        finish();
    }

    public void verifyStoragePermissions() {
        int REQUEST_EXTERNAL_STORAGE = 1;

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO};
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
