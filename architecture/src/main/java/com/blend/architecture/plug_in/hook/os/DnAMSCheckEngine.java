package com.blend.architecture.plug_in.hook.os;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.blend.architecture.plug_in.hook.ProxyActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 专门处理绕过AMS检测，让LoginActivity可以正常通过
 */
public class DnAMSCheckEngine {

    /**
     * TODO 此方法 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
     * @param mContext
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public static void mHookAMS(final Context mContext) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // 公共区域
        Object mIActivityManagerSingleton = null; // TODO 公共区域 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本
        Object mIActivityManager = null; // TODO 公共区域 适用于 21以下的版本 以及 21_22_23_24_25  26_27_28 等系统版本

        if (AndroidSdkVersion.isAndroidOS_26_27_28_30()) {
            // 获取系统的 IActivityManager.aidl
            Class mActivityManagerClass = Class.forName("android.app.ActivityManager");
            mIActivityManager = mActivityManagerClass.getMethod("getService").invoke(null); //这是static方法


            // 获取IActivityManagerSingleton
            Field mIActivityManagerSingletonField = mActivityManagerClass.getDeclaredField("IActivityManagerSingleton");
            mIActivityManagerSingletonField.setAccessible(true);
            mIActivityManagerSingleton = mIActivityManagerSingletonField.get(null);

        } else if (AndroidSdkVersion.isAndroidOS_21_22_23_24_25()) {
            Class mActivityManagerClass = Class.forName("android.app.ActivityManagerNative");
            Method getDefaultMethod = mActivityManagerClass.getDeclaredMethod("getDefault");
            getDefaultMethod.setAccessible(true);
            mIActivityManager = getDefaultMethod.invoke(null);

            //gDefault
            Field gDefaultField = mActivityManagerClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            mIActivityManagerSingleton = gDefaultField.get(null);
        }

        //获取动态代理
        Class mIActivityManagerClass = Class.forName("android.app.IActivityManager");
        final Object finalMIActivityManager = mIActivityManager;
        Object mIActivityManagerProxy =  Proxy.newProxyInstance(mContext.getClassLoader(),
                new Class[]{mIActivityManagerClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Log.e("startActivity", "startActivity: 111" + method.getName());
                        if ("startActivity".equals(method.getName())) {
                            // 把LoginActivity 换成 ProxyActivity
                            // TODO 把不能经过检测的LoginActivity 替换 成能够经过检测的ProxyActivity
                            // 使用占坑Activity通过AMS的检测
                            Intent proxyIntent = new Intent(mContext, ProxyActivity.class);


                            // 把目标的LoginActivity 取出来 携带过去
                            Intent target = (Intent) args[2];
                            proxyIntent.putExtra(Parameter.TARGET_INTENT, target);
                            args[2] = proxyIntent;
                        }

                        return method.invoke(finalMIActivityManager, args);
                    }
                });

        if (mIActivityManagerSingleton == null || mIActivityManagerProxy == null) {
            throw new IllegalStateException("实在是没有检测到这种系统，需要对这种系统单独处理...");
        }

        Class mSingletonClass = Class.forName("android.util.Singleton");

        Field mInstanceField = mSingletonClass.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        // 把系统里面的 IActivityManager 换成 我们自己写的动态代理
        mInstanceField.set(mIActivityManagerSingleton, mIActivityManagerProxy);
    }

}
