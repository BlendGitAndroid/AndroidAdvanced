package com.blend.optimization;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.blend.optimization.hotfix.utils.FixDexUtils;

//注意使用的时候要初始化
public class OptimizationApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        FixDexUtils.loadFixedDex(this);
    }
}
