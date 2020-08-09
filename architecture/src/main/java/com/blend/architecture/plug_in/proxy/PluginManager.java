package com.blend.architecture.plug_in.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;

public class PluginManager {

    private static final PluginManager ourInstance = new PluginManager();

    private DexClassLoader dexClassLoader;

    private Resources resources;

    private PackageInfo packageInfo;

    public static PluginManager getInstance() {
        return ourInstance;
    }

    public PluginManager() {
    }

    public void loadPath(Context context) {
        File filesDir = context.getDir("plugin", Context.MODE_PRIVATE);
        String name = "plugin.apk";
        String path = new File(filesDir, name).getAbsolutePath();

        PackageManager packageManager = context.getPackageManager();
        packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

        //activity
        File dex = context.getDir("dex", Context.MODE_PRIVATE);
        dexClassLoader = new DexClassLoader(path, dex.getAbsolutePath(), null, context.getClassLoader());

        //resource
        try {
            AssetManager manager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(manager, path);
            resources = new Resources(manager,
                    context.getResources().getDisplayMetrics(),
                    context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

        parseReceivers(context, path);
    }

    // 解析插件静态广播
    private void parseReceivers(Context context, String path) {
        try {

            //获取PackageParser
            Class packageParserClass = Class.forName("android.content.pm.PackageParser");
            //创建PackageParser对象
            Object packageParser = packageParserClass.newInstance();
            //获取PackageParser中的parsePackage（）
            Method parsePackageMethod =
                    packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            //调用parsePackage（） 返回Package
            Object packageObj = parsePackageMethod.invoke(packageParser, new File(path), PackageManager.GET_ACTIVITIES);

            //通过Package 来获取这个对象中的成员变量（receivers）==== 》receivers 的集合
            Field receiversField = packageObj.getClass().getDeclaredField("receivers");
            List receivers = (List) receiversField.get(packageObj);


            //获取Component 为的是获取IntentFilter集合
            Class<?> componentClass = Class.forName("android.content.pm.PackageParser$Component");
            Field intentsField = componentClass.getDeclaredField("intents");


            // 调用generateActivityInfo 方法, 把PackageParser.Activity 转换成
            Class<?> packageParser$ActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
//            generateActivityInfo方法
            Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            Object defaltUserState = packageUserStateClass.newInstance();
            Method generateReceiverInfo = packageParserClass.getDeclaredMethod("generateActivityInfo",
                    packageParser$ActivityClass, int.class, packageUserStateClass, int.class);

            //反射获取UserID
            Class<?> userHandler = Class.forName("android.os.UserHandle");
            Method getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId");
            int userId = (int) getCallingUserIdMethod.invoke(null);

            for (Object activity : receivers) {
                ActivityInfo info = (ActivityInfo) generateReceiverInfo.invoke(packageParser, activity, 0, defaltUserState, userId);
                List<? extends IntentFilter> intentFilters =
                        (List<? extends IntentFilter>) intentsField.get(activity);

                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) dexClassLoader.loadClass(info.name).newInstance();
                for (IntentFilter intentFilter : intentFilters) {
                    context.registerReceiver(broadcastReceiver, intentFilter);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Resources getResources() {
        return resources;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }
}
