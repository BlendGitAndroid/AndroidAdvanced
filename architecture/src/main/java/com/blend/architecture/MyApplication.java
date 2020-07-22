package com.blend.architecture;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatDelegate;

import com.blend.architecture.change_skin.NightModeConfig;
import com.blend.architecture.database_design.daopackage.DaoMaster;
import com.blend.architecture.database_design.daopackage.DaoSession;
import com.blend.skincore.SkinManager;


public class MyApplication extends Application {

    private static MyApplication sMyApplication;
    private static DaoSession sDaoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        sMyApplication = this;

        initSkin();

        initGreenDao();
    }

    public static MyApplication getInstance() {
        return sMyApplication;
    }

    private void initSkin() {
        SkinManager.init(sMyApplication);

        //根据app上次退出的状态来判断是否需要设置夜间模式,提前在SharedPreference中存了一个是
        // 否是夜间模式的boolean值
        boolean isNightMode = NightModeConfig.getInstance().getNightMode(getApplicationContext());
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void initGreenDao() {
        //创建数据库xuhai.db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(sMyApplication, "xuhai.db");
        //获取可写数据库
        SQLiteDatabase database = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(database);
        //获取Dao对象管理者
        sDaoSession = daoMaster.newSession();
    }

    /**
     * 获取操作类DaoSession，默认数据库表存储在内存里
     */
    public static DaoSession getDaoSession() {
        return sDaoSession;
    }


}
