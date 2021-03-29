package com.blend.architecture.plug_in.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 动态加载技术：
 * 原理：在应用程序运行时，动态加载一些程序中原本不存在的可执行文件并运行这些文件里的代码逻辑。可执行文件总的来说分为两个，一种是动态
 * 链接库so，另一种是dex相关文件（dex文件包含jar/apk文件）。
 * 分类：
 * 1.插件化技术：主要用于解决应用越来越庞大的以及功能模块的解耦,所以小项目中一般用的不多
 * 2.热修复技术：主要用来修复bug
 * 实现插件化的方式：
 * 1.插桩式（接口回调）
 * 2.Hook技术
 * <p>
 * <p>
 * 插桩式实现方式：
 * 1.设计插件的标准接口。创建一个lib module,创建一个接口,主要用来传递给插件Context和管理插件的生命周期，如pluginstand module。
 * 2.创建一个baseActivity和baseService实现特定一个的接口，接收context，并重写跟context有关的方法，插件中的Activity和service都继承各自的基类。
 * 3.创建一个PluginManager的单例类，管理插件方法,获取插件的packageInfo,resources,dexClassLoader等，获取Resources时要通过反射调用
 * AssetManager中的addAssetPath方法将插件的路径传给assetManager。
 * 4.在application中提前加载插件，并将插件复制到app的私有目录。
 * 5.从宿主跳转到插件的activity时,先跳转到插桩的activity中并携带上实际要调转的插件的activity类。在插桩的activity中解析实际要调转的activity并反
 * 射获取该类对象强转为定义的接口，并将activity的生命周期在对应的方法中调用传递给插件。
 * <p>
 * 插件化有三种实现方式：
 * HOOK：hook IActivityManager和hook Instrumentation，这两个不同的是前者是由动态代理实现的，因为前者是接口是可以通过动态代理来实现的，
 * 后者是静态代理的。这种方式的Activity是有生命周期的，因为mToken机制，并且是有context的，可以在插件APP中生成AssetManager和Resource。
 * 还有一种方式是通过接口的方式：这种方式是没有context的，需要通过接口传递生命周期和context。在插件的Activity中的context就是ProxyActivity
 * 的Context。然后重写类加载器，重写AssetManager，重写Resources类进行插件资源的回调，重写生命周期进行回调。在插件类中调用context的一些方法
 * 都是回调到宿主的Activity，其实就是两边进行相互回调。宿主Activity回调自定义接口到插件Activity，插件Activity回调Context到宿主实现，是有一定的局限性。
 * 最后一种是反射，因为性能原因，没有框架使用。
 */
public class PlugInMainActivity extends AppCompatActivity {

    private static final String TAG = "PlugInMainActivity";

    static final String ACTION = "com.blend.tao.dynamic.broadcast.StaticReceiver";

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, " 我是宿主，收到你的消息,握手完成!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_in_main);
        registerReceiver(mReceiver, new IntentFilter(ACTION));
    }


    public void load(View view) {
        loadPlugin();
    }

    //将apk加载到私有目录
    private void loadPlugin() {
        ///data/user/0/com.blend.androidadvanced/app_plugin
        File filesDir = this.getDir("plugin", Context.MODE_PRIVATE);
        String name = "plugin.apk";
        String filePath = new File(filesDir, name).getAbsolutePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        InputStream is = null;
        FileOutputStream os = null;
        try {
            Log.e(TAG, "加载插件 " + new File(Environment.getExternalStorageDirectory(), name).getAbsolutePath());
            is = new FileInputStream(new File(Environment.getExternalStorageDirectory(), name));
            os = new FileOutputStream(filePath);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            File f = new File(filePath);
            if (f.exists()) {
                Toast.makeText(this, "dex overwrite", Toast.LENGTH_SHORT).show();
            }
            PluginManager.getInstance().loadPath(this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void click(View view) {
        Intent intent = new Intent(this, ProxyActivity.class);
        //获取插件APK的第一个Activity的类名
        intent.putExtra("className", PluginManager.getInstance().getPackageInfo().activities[0].name);
        startActivity(intent);
    }

    public void sendBroadCast(View view) {
        Intent intent = new Intent();
        intent.setAction("com.blend.tao.static.StaticReceiver");
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}