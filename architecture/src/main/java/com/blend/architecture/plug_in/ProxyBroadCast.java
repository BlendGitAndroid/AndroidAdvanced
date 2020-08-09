package com.blend.architecture.plug_in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.example.pluginstand.PayInterfaceBroadcast;

import java.lang.reflect.Constructor;

public class ProxyBroadCast extends BroadcastReceiver {
    /**
     * app 启动
     * 手机启动
     */

    public ProxyBroadCast() {
    }

    //需要加载插件的全类名
    private String className;
    private PayInterfaceBroadcast payInterfaceBroadcast;

    public ProxyBroadCast(String className, Context context) {
        this.className = className;

        try {
            Class<?> aClass = PluginManager.getInstance().getDexClassLoader().loadClass(className);
            Constructor constructor = aClass.getConstructor(new Class[]{});
            Object in = constructor.newInstance(new Object[]{});
            payInterfaceBroadcast = (PayInterfaceBroadcast) in;
            payInterfaceBroadcast.attach(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        payInterfaceBroadcast.onReceive(context, intent);
    }
}
