package com.blend.optimization.hotfix.utils;

import android.content.Context;

import java.io.File;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixDexUtils {
    //存放需要修复的dex集合
    private static HashSet<File> loadedDex = new HashSet<>();

    static {
        //修复前先清空
        loadedDex.clear();
    }

    public static void loadFixedDex(Context context) {
        if (context == null)
            return;
        //dex文件目录
        //data/user/0/com.blend.androidadvanced/app_odex
        //拿到hotfix的dex文件
        File fileDir = context.getDir("odex", Context.MODE_PRIVATE);
        //依次取出dex下面的文件
        File[] files = fileDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".dex") && !"classes.dex".equals(file.getName())) {
                //找到要修复的dex文件
                loadedDex.add(file);
            }
        }
        //创建类加载器
        createDexClassLoader(context, fileDir);
    }

    /**
     * 创建类加载器
     *
     * @param context
     * @param fileDir
     */
    private static void createDexClassLoader(Context context, File fileDir) {
        String optimizedDirectory = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        //创建出优化后的dex目录
        File fOpt = new File(optimizedDirectory);
        if (!fOpt.exists()) {
            fOpt.mkdirs();
        }
        DexClassLoader classLoader;
        for (File dex : loadedDex) {
            //初始化类加载器
            //参数：
            //dex文件的路径(多个，默认以:隔开);
            //缓存optimizedDirectory
            //本地库的路径(多个)
            //父加载器
            classLoader = new DexClassLoader(dex.getAbsolutePath(), optimizedDirectory, null,
                    context.getClassLoader());
            //热修复
            hotFix(classLoader, context);
        }
    }

    private static void hotFix(DexClassLoader myClassLoader, Context context) {
        //系统的类加载器
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try {
            //重要的来了
            // 获取自己的DexElements数组对象
            Object myDexElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(myClassLoader));
            // 获取系统的DexElements数组对象
            Object sysDexElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(pathClassLoader));
            // 合并
            Object dexElements = ArrayUtils.combineArray(myDexElements, sysDexElements);
            // 获取系统的 pathList
            Object sysPathList = ReflectUtils.getPathList(pathClassLoader);
            // 重新赋值给系统的 pathList
            ReflectUtils.setField(sysPathList, sysPathList.getClass(), dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
