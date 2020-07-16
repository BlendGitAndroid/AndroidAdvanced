package com.blend.architecture.change_skin;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blend.architecture.R;
import com.blend.architecture.change_skin.skin.Skin;
import com.blend.architecture.change_skin.skin.SkinUtils;
import com.blend.skincore.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * LayoutInflater的创建过程及实际类型？
 * 系统在Context中默认提供了三种获取LayoutInflate的方式
 * 1)LayoutInflater layoutInflater = getLayoutInflater();若2）中的context是activity，则1）和2）是一样的，因为最终调用的是PhoneWindow的
 * LayoutInflater.from(context);而这个context正是activity。
 * 2)LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
 * 3)LayoutInflater systemServiceInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 * <p>
 * 从LayoutInflater.from(context)来分析，会调用Context的getSystemService抽象方法，注意这里的context，这里可以是Activity，Application或Service，
 * 那他们有什么区别呢？
 * 1）以Activity为例，Activity的父类ContextThemeWrapper重写了getSystemService方法，每个Activity都有一个独立的LayoutInflater，
 * 通过LayoutInflater.from(getBaseContext()).cloneInContext(this)创建出来，而getBaseContext()实际上是ContextImpl，是在ActivityThread中创建出来
 * 关联到ContextImpl中的，故getSystemService实际上是调用ContextImpl中的getSystemService方法，最终调用系统的SystemServiceRegistry.getSystemService(this, name)
 * 来根据单例模式获取到LayoutInflater，这个LayoutInflater实际上是PhoneLayoutInflater。然后通过cloneInContext(this)生成每一个Activity独有的LayoutInflate,
 * 之所以叫做 “clone”，是因为：系统默认会将进程级的 LayoutInflater 配置给每个 Activity 的 LayoutInflater，这也符合了 LayoutInflater 的自我介绍 “且正确配置的
 * 标准 LayoutInflater”。
 * 2）由于 Application和Service都是ContextWrapper的直接子类，它们并没有对getSystemService方法做单独处理。故都是通过 ContextImpl获取的同一个，
 * 也就是保存在 SystemServiceRegistry中的LayoutInflater，也就是PhoneLayoutInflater。
 */

public class SkinActivity extends AppCompatActivity {
    /**
     * 从服务器拉取的皮肤表
     */
    List<Skin> skins = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
//
        skins.add(new Skin("e0893ca73a972d82bcfc3a5a7a83666d", "1111111.skin", "app_skin-debug.apk"));
    }

    /**
     * 下载皮肤包
     */
    private void selectSkin(Skin skin) {
        //应用包名文件目录
        File theme = new File(getFilesDir(), "theme");
        if (theme.exists() && theme.isFile()) {
            theme.delete();
        }
        theme.mkdirs();
        File skinFile = skin.getSkinFile(theme);
        if (skinFile.exists()) {
            Log.e("SkinActivity", "皮肤已存在,开始换肤");
            return;
        }
        Log.e("SkinActivity", "皮肤不存在,开始下载");
        FileOutputStream fos = null;
        InputStream is = null;
        //临时文件
        File tempSkin = new File(skinFile.getParentFile(), skin.name + ".temp");
        try {
            fos = new FileOutputStream(tempSkin);
            //假设下载皮肤包
            is = getAssets().open(skin.url);
            byte[] bytes = new byte[10240];
            int len;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            //下载成功，将皮肤包信息insert已下载数据库
            Log.e("SkinActivity", "皮肤包下载完成开始校验");
            //皮肤包的md5校验 防止下载文件损坏(但是会减慢速度。从数据库查询已下载皮肤表数据库中保留md5字段)
            if (TextUtils.equals(SkinUtils.getSkinMD5(tempSkin), skin.md5)) {
                Log.e("SkinActivity", "校验成功,修改文件名。");
                tempSkin.renameTo(skinFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tempSkin.delete();
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void changeSkin(View view) {
// //        //使用第0个皮肤
//         Skin skin = skins.get(0);
//         selectSkin(skin);
// //        //换肤
//         SkinManager.getInstance().loadSkin(skin.path);


        String path = Environment.getExternalStorageDirectory().getPath() + "/skincoreapp-debug.apk";
        //SkinManager.getInstance().loadSkin("/sdcard/app-skin-debug.skin");
        SkinManager.getInstance().loadSkin(path);

    }

    public void restoreSkin(View view) {
        SkinManager.getInstance().loadSkin(null);
    }

    /**
     * 夜间模式
     *
     * @param view
     */
    public void night(View view) {
        //获取当前的模式，设置相反的模式，这里只使用了，夜间和非夜间模式
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentMode != Configuration.UI_MODE_NIGHT_YES) {
            //保存夜间模式状态,Application中可以根据这个值判断是否设置夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            //ThemeConfig主题配置，这里只是保存了是否是夜间模式的boolean值
            NightModeConfig.getInstance().setNightMode(getApplicationContext(), true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            NightModeConfig.getInstance().setNightMode(getApplicationContext(), false);
        }

        recreate();//需要recreate才能生效
    }

    /**
     * 日间模式
     *
     * @param view
     */
    public void day(View view) {
        //获取当前的模式，设置相反的模式，这里只使用了，夜间和非夜间模式
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentMode != Configuration.UI_MODE_NIGHT_YES) {
            //保存夜间模式状态,Application中可以根据这个值判断是否设置夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            //ThemeConfig主题配置，这里只是保存了是否是夜间模式的boolean值
            NightModeConfig.getInstance().setNightMode(getApplicationContext(), false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            NightModeConfig.getInstance().setNightMode(getApplicationContext(), true);
        }

        recreate();//需要recreate才能生效
    }
}
