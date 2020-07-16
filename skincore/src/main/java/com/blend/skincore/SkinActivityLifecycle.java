package com.blend.skincore;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;

import com.blend.skincore.utils.SkinThemeUtils;

import java.lang.reflect.Field;

/**
 * ActivityLifecycleCallbacks用来监听所有Activity的生命周期回调
 * 在android中的应用：
 * 1.应用新开进程假重启处理（低内存回收、修改权限）
 * 2.管理 Activity 页面栈
 * 3.获取当前 Activity 页面
 * 4.判断应用前后台
 * 5.保存恢复状态值 savedInstanceState
 * 6.页面分析统计埋点
 */
public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new
            ArrayMap<>();


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        /**
         *  更新状态栏
         */
        SkinThemeUtils.updateStatusBarColor(activity);
        /**
         * 更新字体
         * Typeface.DEFAULT //常规字体类型
         */
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
        /**
         *  更新布局视图
         */
        //获得Activity的布局加载器
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        try {
            //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            //如设置过抛出一次
            //设置 mFactorySet 标签为false
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        LayoutInflater内部为开发者提供了直接设置 Factory的方法，不过需要注意该方法只能被设置一次，否则将会抛出异常。
        可以利用反射将mFactorySet修改为false加载。
         */
        //使用factory2 设置布局加载工程
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory
                (activity, typeface);
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);
        //添加观察者
        SkinManager.getInstance().addObserver(skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }


    public void updateSkin(Activity activity) {
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = mLayoutInflaterFactories.get(activity);
        skinLayoutInflaterFactory.update(null, null);
    }
}
