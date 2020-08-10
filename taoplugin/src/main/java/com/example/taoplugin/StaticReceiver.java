package com.example.taoplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StaticReceiver extends BroadcastReceiver {
    static final String ACTION = "com.blend.tao.dynamic.broadcast.StaticReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("StaticReceiver", "我是插件   收到宿主的消息  静态注册的广播  收到宿主的消息");
        context.sendBroadcast(new Intent(ACTION));
    }
}
